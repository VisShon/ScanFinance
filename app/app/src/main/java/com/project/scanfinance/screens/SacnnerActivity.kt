
package com.project.scanfinance.screens

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.project.scanfinance.R
import com.project.scanfinance.database.Expense
import com.project.scanfinance.database.ExpenseDAO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.sql.DriverManager.println
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

fun uploadImageAndSaveExpense(context: Context, imageUri: Uri, dao: ExpenseDAO,coroutineScope: CoroutineScope) {
    val imageFile = File(imageUri.path!!)
    val requestBody = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
    val body = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", imageFile.name, requestBody)
        .build()

    val request = Request.Builder()
        .url("http://localhost:5000/upload")
        .post(body)
        .build()

    coroutineScope.launch{
        parseExpense(context,dao)
    }

    OkHttpClient().newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Toast.makeText(context, "Error uploading image: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        override fun onResponse(call: Call, response: Response) {
            response.body?.let { responseBody ->
                val responseData = responseBody.string()
                println("Response: $responseData" + "Potty")
                coroutineScope.launch{
                    parseExpense(context,dao)
                }
                Toast.makeText(context, "Expense saved successfully", Toast.LENGTH_SHORT).show()
            }
        }
    })
}

suspend fun parseExpense(context: Context,dao: ExpenseDAO) {

    val jsonString = """
    {
        "receipt": {
            "store": "The Lone Pine",
            "address": "43 Manchester Road",
            "city": "Brisbane",
            "country": "Australia",
            "phone": "617-3236-6207",
            "invoice": "Invoice 08000008",
            "date": "09/04/08",
            "table": "Table",
            "items": [
                {
                    "name": "Carlsberg Bottle",
                    "price": 16.00,
                    "quantity": 2
                },
                {
                    "name": "Heineken Draft Standard.",
                    "price": 15.20,
                    "quantity": 1
                },
                {
                    "name": "Carlsberg Bucket (5 bottles).",
                    "price": 80.00,
                    "quantity": 1
                },
                {
                    "name": "Grilled Chicken Breast.",
                    "price": 74.00,
                    "quantity": 1
                },
                {
                    "name": "Sirloin Steak",
                    "price": 96.00,
                    "quantity": 1
                },
                {
                    "name": "Coke",
                    "price": 3.50,
                    "quantity": 1
                },
                {
                    "name": "Ice Cream",
                    "price": 18.00,
                    "quantity": 5
                }
            ],
            "subtotal": 327.30,
            "tax": 16.36,
            "service_charge": 32.73,
            "total": 400.00
        },
        "customer": {
            "name": "John",
            "phone": "617-3236-6207"
        }
    }
"""

    val current = LocalDate.now().toString()
//    val jsonData = file.readText(StandardCharsets.UTF_8)
    val jsonObject = JSONObject(jsonString)
    val receipt = jsonObject.getJSONObject("receipt")

    val storeName = receipt.getString("store")
    val total = receipt.getDouble("total")

    println("Store Name: $storeName")
    println("Total Price: $total")

    val newExpense = Expense(
        date = current,
        paymentTo =  storeName,
        paymentFrom = "Vishnu",
        amountPaid = total
    )
    dao.insertExpense(newExpense)
}

fun createImageFile(context: Context): File? {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
}

@Composable
fun ScannerActivity(dao: ExpenseDAO) {
    val context = LocalContext.current
    var imageUri: Uri? by remember { mutableStateOf(null) }
    var hasCameraPermission by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasCameraPermission = isGranted
        }
    )

    // Launch the camera to take a picture
    val startCamera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            Toast.makeText(context, "Image saved to: $imageUri", Toast.LENGTH_LONG).show()
            coroutineScope.launch{
                parseExpense(context,dao)
            }
//            imageUri?.let { uri ->
//                uploadImageAndSaveExpense(context, uri, dao, coroutineScope)
//            }
        } else {
            Toast.makeText(context, "Failed to take image", Toast.LENGTH_SHORT).show()
        }
    }

    // Check and request permission
    LaunchedEffect(key1 = true) {
        hasCameraPermission = context.checkSelfPermission(Manifest.permission.CAMERA) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED

        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // UI layout
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        if (!hasCameraPermission) {
            Button(
                onClick = {
                    if (hasCameraPermission) {
                        imageUri = createImageFile(context)?.let { FileProvider.getUriForFile(context, "${context.packageName}.provider", it) }
                        openCamera(startCamera, context)
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39AEA8)),
            ) {
                Text("Request Camera Permission")
            }
        } else {
            Button(
                onClick = { openCamera(startCamera, context) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39AEA8)),
            ) {
                Text("Scan a Receipt")
            }
        }
    }
}

// Function to handle opening the camera
fun openCamera(startCamera: ActivityResultLauncher<Uri>, context: Context) {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
    val photoFile: File = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    val imageUri: Uri = FileProvider.getUriForFile(context, "${context.applicationContext.packageName}.provider", photoFile)

    startCamera.launch(imageUri)
}
