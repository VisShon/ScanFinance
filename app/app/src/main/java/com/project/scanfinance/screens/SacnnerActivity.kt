
package com.project.scanfinance.screens

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.sql.DriverManager.println
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import java.util.concurrent.TimeUnit

fun uploadImageAndSaveExpense(context: Context, imageUri: Uri, dao: ExpenseDAO,coroutineScope: CoroutineScope) {
    val client = getCustomOkHttpClient()
    val contentResolver = context.contentResolver
    val inputStream = try {
        contentResolver.openInputStream(imageUri)
    } catch (e: FileNotFoundException) {
        Log.e("W", "File not found: $imageUri", e)
        return
    }

    val tempFile = createTemporaryFile(context)
    inputStream?.use { input ->
        FileOutputStream(tempFile).use { fileOut ->
            input.copyTo(fileOut)
        }
    }

    val requestBody = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
    val body = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", tempFile.name, requestBody)
        .build()

    val request = Request.Builder()
        .url("http://192.168.244.125:5000/upload")
        .post(body)
        .build()


    Log.d("W","Bulilding Response" )
    coroutineScope.launch(Dispatchers.IO) {
        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                response.body?.let { responseBody ->
                    val responseContent = responseBody.string()
                    launch(Dispatchers.Main) {
                        parseExpense(responseContent, dao)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("UploadImage", "Error uploading image: ${e.message}")
        }
    }
}

suspend fun parseExpense(data: String, dao: ExpenseDAO) {
    Log.d("W",data)
    val current = LocalDate.now().toString()
    val jsonObject = JSONObject(data)
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
//            coroutineScope.launch{
//                uploadImageAndSaveExpense(context, uri, dao, coroutineScope)
//            }
            imageUri?.let { uri ->
                uploadImageAndSaveExpense(context, uri, dao, coroutineScope)
            }
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
        }else{
            imageUri = createImageUri(context)
            println("Initial Check: Image URI set to $imageUri")
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
                        imageUri = createImageUri(context)  // Ensure URI is refreshed/recreated before use
                        println("Button Pressed: Image URI refreshed to $imageUri")
                        imageUri?.let { uri ->
                            startCamera.launch(uri)
                        }
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

fun openCamera(startCamera: ActivityResultLauncher<Uri>, context: Context) {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
    val photoFile: File = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    val imageUri: Uri? = createImageUri(context)

    if (imageUri != null) {
        startCamera.launch(imageUri)
    }
}

fun createImageUri(context: Context): Uri? {
    val photoFile: File? = createImageFile(context)
    return photoFile?.let {
        FileProvider.getUriForFile(context, "${context.packageName}.provider", it)
    }
}

fun createTemporaryFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("JPEG_temp_", ".jpg", storageDir)
}

fun getCustomOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.MINUTES)
        .readTimeout(5, TimeUnit.MINUTES)
        .writeTimeout(5, TimeUnit.MINUTES)
        .build()
}
