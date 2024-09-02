import os
from dotenv import load_dotenv

load_dotenv()

class Config:
    AWS_ACCESS_KEY_ID = os.getenv("AWS_ACCESS_KEY_ID")
    AWS_SECRET_ACCESS_KEY = os.getenv("AWS_SECRET_ACCESS_KEY")
    S3_BUCKET_NAME = os.getenv("S3_BUCKET_NAME")

    SARAMINCONNECTOR_API_KEY = os.getenv("SARAMIN_API_KEY")
    SARAMINCONNECTOR_URL = os.getenv("SARAMIN_URL")

    SEOULJOBPORTALCONNECTOR_API_KEY = os.getenv("SEOUL_API_KEY")
    SEOULJOBPORTALCONNECTOR_URL = os.getenv("SEOUL_URL")

    GGJOBABACONNECTOR_API_KEY = os.getenv("GYEONGGI_API_KEY")
    GGJOBABACONNECTOR_URL = os.getenv("GYEONGGI_URL")
    
    PUBLICINSTITUTIONCONNECTOR_API_KEY = os.getenv("PUBLIC_INSTITUTION_API_KEY")
    PUBLICINSTITUTIONCONNECTOR_URL = os.getenv("PUBLIC_INSTITUTION_URL")