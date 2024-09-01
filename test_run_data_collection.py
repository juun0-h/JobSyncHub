import pendulum
from src.connectors.ggjobaba import GGJobabaConnector
from src.connectors.seoul_job_portal import SeoulJobPortalConnector
from src.connectors.public_institution import PublicInstitutionConnector
from src.connectors.saramin import SaraminConnector
from src.utils.s3_utils import upload_to_s3
from src.utils.config import Config

KST = pendulum.timezone("Asia/Seoul")

def fetch_and_upload_data(connector_class):
    connector = connector_class()
    data = connector.fetch_data()
    date_str = pendulum.now(KST).format('YYYY-MM-DD')
    filename = f"{connector_class.__name__}/{date_str}.json"
    
    if connector_class.__name__ == "GGJobabaConnector":
        api_key = Config.GYEONGGI_API_KEY
        url = Config.GYEONGGI_URL
    elif connector_class.__name__ == "SeoulJobPortalConnector":
        api_key = Config.SEOUL_API_KEY
        url = Config.SEOUL_URL
    elif connector_class.__name__ == "PublicInstitutionConnector":
        api_key = Config.PUBLIC_INSTITUTION_API_KEY
        url = Config.PUBLIC_INSTITUTION_URL
    elif connector_class.__name__ == "SaraminConnector":
        api_key = Config.SARAMIN_API_KEY
        url = Config.SARAMIN_URL
    else:
        raise ValueError(f"Unknown connector class: {connector_class.__name__}")
    
    config = {
        "api_key": api_key,
        "url": url,
        "source": connector_class.__name__,
        "last_updated": pendulum.now(KST).isoformat()
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