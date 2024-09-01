import os
from dotenv import load_dotenv

load_dotenv()

class Config:
    AWS_ACCESS_KEY_ID = os.getenv("AWS_ACCESS_KEY_ID")
    AWS_SECRET_ACCESS_KEY = os.getenv("AWS_SECRET_ACCESS_KEY")
    S3_BUCKET_NAME = os.getenv("S3_BUCKET_NAME")

    SARAMIN_API_KEY = os.getenv("SARAMIN_API_KEY")
    SARAMIN_URL = os.getenv("SARAMIN_URL")

    SEOUL_API_KEY = os.getenv("SEOUL_API_KEY")
    SEOUL_URL = os.getenv("SEOUL_URL")

    GYEONGGI_API_KEY = os.getenv("GYEONGGI_API_KEY")
    GYEONGGI_URL = os.getenv("GYEONGGI_URL")
    
    PUBLIC_INSTITUTION_API_KEY = os.getenv("PUBLIC_INSTITUTION_API_KEY")
    PUBLIC_INSTITUTION_URL = os.getenv("PUBLIC_INSTITUTION_URL")