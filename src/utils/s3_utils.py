import json
import os
import boto3
from typing import List, Dict, Any
from dotenv import load_dotenv

load_dotenv()   # .env 파일 load

# S3 client 생성
s3_client = boto3.client(
    's3',
    aws_access_key_id=os.getenv('AWS_ACCESS_KEY_ID'),
    aws_secret_access_key=os.getenv('AWS_SECRET_ACCESS_KEY')
)

def upload_to_s3(data: List[Dict[Any, Any]], filename: str) -> None:
    """
    S3 bucket에 data upload

    ------
    Params:
        data (List[Dict[Any, Any]]): Upload할 data list
        filename (str): file name
    """
    try:
        s3_client.put_object(
            Bucket=os.getenv('S3_BUCKET_NAME'),
            Key=filename,
            Body=json.dumps(data, ensure_ascii=False, indent=2).encode('utf-8'),
            ContentType='application/json'
        )
        print(f"Successfully uploaded {os.getenv('S3_BUCKET_NAME')}/{filename} to S3")
    except Exception as e:
        print(f"Error uploading to S3: {e}")