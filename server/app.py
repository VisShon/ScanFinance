import re
import torch
from flask import Flask, json, request, jsonify
from paddleocr import PaddleOCR, draw_ocr
import numpy as np
from PIL import Image
from ast import literal_eval
import bitsandbytes as bnb
import accelerate
from transformers import AutoModelForCausalLM, AutoTokenizer, GenerationConfig, BitsAndBytesConfig
import os
import gc

print(accelerate.__version__)
print(bnb.__version__)

app = Flask(__name__)

FALLBACK_IMAGE_DIR = './test_data'
FALLBACK_IMAGE_NAME = 'test.png'
ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg'}


def allowed_file(filename):
	return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

def paddle_scan(paddleocr,img_path_or_nparray):
	result = paddleocr.ocr(img_path_or_nparray,cls=True)
	result = result[0]
	boxes = [line[0] for line in result]       #boundign box 
	txts = [line[1][0] for line in result]     #raw text
	scores = [line[1][1] for line in result]   # scores
	return  txts, result

def clear_gpu_memory():
    torch.cuda.empty_cache()
    gc.collect()

@app.route('/upload', methods=['POST'])
def upload_file():
	if 'file' in request.files and request.files['file'].filename != '':
		file = request.files['file']
		if file:
			image = Image.open(file.stream)
	else:
		fallback_image_path = os.path.join(FALLBACK_IMAGE_DIR, FALLBACK_IMAGE_NAME)
		if not os.path.exists(fallback_image_path):
			return jsonify({'error': 'Fallback image not found'}), 404
		image = Image.open(fallback_image_path)

	receipt_image_array = np.array(image.convert('RGB'))
	paddleocr = PaddleOCR(lang="en",ocr_version="PP-OCRv4",show_log = False,use_gpu=True)
	receipt_texts, receipt_boxes = paddle_scan(paddleocr,receipt_image_array)

	print(receipt_boxes)
	clear_gpu_memory()

	bnb_config = BitsAndBytesConfig(
		llm_int8_enable_fp32_cpu_offload=True,
		load_in_4bit=True,
		bnb_4bit_use_double_quant=True,
		bnb_4bit_quant_type="nf4",
		bnb_4bit_compute_dtype=torch.float32,
	)

	device_map = {
		"transformer.word_embeddings": 0,
		"transformer.word_embeddings_layernorm": 0,
		"lm_head": 0,
		"transformer.h": 0,
		"transformer.ln_f": 0,
		"model.embed_tokens": 0,
		"model.layers":0,
		"model.norm":0  
	}

	device = "cuda" if torch.cuda.is_available() else "cpu"
	model_id="mychen76/mistral7b_ocr_to_json_v1"
	model = AutoModelForCausalLM.from_pretrained(
		model_id, 
		trust_remote_code=True,  
		torch_dtype=torch.float16,
		quantization_config=bnb_config,
		device_map=device_map,
	)

	tokenizer = AutoTokenizer.from_pretrained(model_id, trust_remote_code=True)

	prompt=f"""### Instruction:
	You are POS receipt data expert, parse, detect, recognize and convert following receipt OCR image result into structure receipt data object. 
	Don't make up value not in the Input. Output must be a well-formed JSON object.```json

	### Input:
	{receipt_boxes}

	### Output:
	"""
	


	with torch.inference_mode():
		inputs = tokenizer(prompt,return_tensors="pt",truncation=True).to(device)
		outputs = model.generate(**inputs, max_new_tokens=1024)
		result_text = tokenizer.batch_decode(outputs)[0]
		print(result_text)
		pattern = r"### Output:\s+({.*?})\s+### Note:"
		match = re.search(pattern, text, re.DOTALL)
		if match:
			json_data = match.group(1)
			json_data = json_data.replace("\t", "")
			try:
				parsed_json = json.loads(json_data)
				return parsed_json
			except json.JSONDecodeError as e:
				print("Error decoding JSON:", e)
				return result_text


if __name__ == '__main__':
	app.run(debug=True)
