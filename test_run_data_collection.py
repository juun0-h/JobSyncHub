import pendulum
from src.connectors.ggjobaba import GGJobabaConnector
from src.connectors.seoul_job_portal import SeoulJobPortalConnector
from src.connectors.public_institution import PublicInstitutionConnector
from src.connectors.saramin import SaraminConnector
from src.utils.s3_utils import upload_to_s3
from src.utils.config import Config

KST = pendulum.timezone("Asia/Seoul")

def fetch_and_upload_data(connector_class):
    """
    특정 connector의 data를 가져와서 S3에 bucket에 upload

    -----
    Param:
        connector_class (class): connector
    """
    connector = connector_class()
    data = connector.fetch_data()
    date_str = pendulum.now(KST).format('YYYY-MM-DD')
    filename = f"{connector_class.__name__}/{date_str}.json"

    config = {
        "api_key": getattr(Config, f"{connector_class.__name__.upper()}_API_KEY"),
        "url": getattr(Config, f"{connector_class.__name__.upper()}_URL"),
        "source": connector_class.__name__,
        "last_updated": date_str
    }
    
    content = {
        "config": config,
        "data": data
    }
    
    upload_to_s3(content, filename)
    print(f"Uploaded data for {connector_class.__name__}, total items: {len(data)}")

if __name__ == "__main__":
    connectors = [
        GGJobabaConnector,
        SeoulJobPortalConnector,
        PublicInstitutionConnector,
        SaraminConnector
    ]

    for connector_class in connectors:
        fetch_and_upload_data(connector_class)

    print("Data collection and upload complete.")