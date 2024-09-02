import requests
from .base import BaseConnector
from ..utils.config import Config
from typing import List, Dict, Any
import pendulum
from ..utils.helpers import convert_date_format

class SaraminConnector(BaseConnector):
    """
    Saramin API, 사람인에서 제공하는 채용 정보를 가져오는 connector class
    """
    def __init__(self):
        """
        Attributes:
            api_key (str): Saramin API key
            base_url (str): Saramin API의 기본 URL
        """
        self.api_key = Config.SARAMINCONNECTOR_API_KEY
        self.base_url = Config.SARAMINCONNECTOR_URL

    def fetch_data(self) -> List[Dict[str, Any]]:
        params = {
            'access-key': self.api_key,
            'count': 110
        }
        response = requests.get(self.base_url, params=params)
        response.raise_for_status()
        data = response.json()['jobs']['job']

        # tmp for testing total count
        # total = response['jobs']['total']
        # print(f"total: {total}")

        for item in data:
            for date_field in self.get_date_fields():
                if date_field in item:
                    timestamp = int(item[date_field])
                    item[date_field] = convert_date_format(
                        pendulum.from_timestamp(timestamp).format('YYYYMMDD'),
                        input_formats=['YYYYMMDD']
                    )

        return data

    def get_date_fields(self) -> List[str]:
        return ['opening-timestamp', 'expiration-timestamp']

    @property
    def source_name(self) -> str:
        return "Saramin"