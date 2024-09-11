import requests
from .base import BaseConnector
from ..utils.config import Config
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

    def fetch_data_and_integrate_date_format(self) -> tuple[list[int, int, str], list[dict[str, any]]]:
        params = {
            'access-key': self.api_key,
            'count': 110,
            'job_mid_cd': 2,
            # 'sort': 'rc'
        }
        response = requests.get(self.base_url, params=params)
        response.raise_for_status()

        header = [response.json()['jobs']['total'],
                  response.json()['jobs']['count'],
                  response.json()['jobs']['start'],
                  ]

        data = response.json()['jobs']['job']

        for item in data:
            for date_field in self.get_date_fields():
                if date_field in item:
                    timestamp = int(item[date_field])
                    item[date_field] = convert_date_format(
                        pendulum.from_timestamp(timestamp).format('YYYYMMDD'),
                        input_formats=['YYYYMMDD']
                    )

        return header, data

    def get_date_fields(self) -> list[str]:
        return ['posting-timestamp', 'modification-timestamp', 'opening-timestamp', 'expiration-timestamp']

    @property
    def source_name(self) -> str:
        return "Saramin"