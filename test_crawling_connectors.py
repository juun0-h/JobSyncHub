#from src.connectors.job_planet import JobPlanetConnector
from src.connectors.wanted import WantedConnector
from src.connectors.jumpit import JumpItConnector
from typing import List, Dict, Any
from pathlib import Path
import time
import json

def test_connector(connector_class):
    """
    각각의 크롤링 connector test

    -----
    Param:
        connector_class (class): connector
    """
    connector = connector_class()
    print(f"\nTesting {connector_class.__name__}:")
    try:
        start = time.time()
        connector.pre_process()
        data = connector.fetch_data_and_integrate_date_format()
        end = time.time()

        # 데이터 저장 data/{connector_class.__name__}_data.json
        save_data(data, f"{connector_class.__name__}_data.json")

        print(f"[{connector_class.__name__}] 총 실행 시간: {end - start:.2f}초")
    except Exception as e:
        print(f"Error occurred: {str(e)}")

def save_data(data: List[Dict[str, Any]], filename) -> None:
    Path('data').mkdir(parents=True, exist_ok=True)

    # 파일을 저장
    filepath = Path('data') / filename
    filepath.write_text(json.dumps(data, ensure_ascii=False, indent=4), encoding='utf-8')

if __name__ == "__main__":
    connectors = [
        #JobPlanetConnector,
        WantedConnector,
        #JumpItConnector
    ]

    for connector_class in connectors:
        test_connector(connector_class)