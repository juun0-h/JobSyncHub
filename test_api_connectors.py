from src.connectors.ggjobaba import GGJobabaConnector
from src.connectors.seoul_job_portal import SeoulJobPortalConnector
from src.connectors.public_institution import PublicInstitutionConnector
from src.connectors.saramin import SaraminConnector

def test_connector(connector_class):
    """
    각각의 connector test

    -----
    Param:
        connector_class (class): connector
    """
    connector = connector_class()
    print(f"\nTesting {connector_class.__name__}:")
    try:
        data = connector.fetch_data()
        print(f"Successfully fetched {len(data)} items.")
        if data:
            print("Sample data:")
            print(data[0])  # 첫 번째 항목만 출력
        else:
            print("No data returned.")
    except Exception as e:
        print(f"Error occurred: {str(e)}")

if __name__ == "__main__":
    connectors = [
        GGJobabaConnector,
        SeoulJobPortalConnector,
        PublicInstitutionConnector,
        SaraminConnector
    ]

    for connector_class in connectors:
        test_connector(connector_class)