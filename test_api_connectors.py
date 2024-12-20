from src.connectors.ggjobaba import GGJobabaConnector
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
        header, data = connector.fetch_data_and_integrate_date_format()
        print(f"Total data: {header[0]}")
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
        PublicInstitutionConnector,
        SaraminConnector
    ]

    for connector_class in connectors:
        test_connector(connector_class)