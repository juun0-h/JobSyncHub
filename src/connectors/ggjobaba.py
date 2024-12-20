import requests
from .base import BaseConnector
from ..utils.config import Config
from ..utils.helpers import convert_dates_in_dict

class GGJobabaConnector(BaseConnector):
    """
    GGJobaba API, 경기도에서 제공하는 채용 정보를 가져오는 connector class
    """

    def __init__(self):
        """
        Attributes:
            api_key (str): GGJobaba API key
            base_url (str): GGJobaba API의 기본 URL
        """
        self.api_key = Config.GGJOBABACONNECTOR_API_KEY
        self.base_url = Config.GGJOBABACONNECTOR_URL

    def fetch_data_and_integrate_date_format(self) -> tuple[list[int], list[dict[str, any]]]:
        params = {
            'KEY': self.api_key,
            'Type': 'json',
            'pSize': 1000
        }
        response = requests.get(self.base_url, params=params)
        response.raise_for_status()

        header = [response.json()['GGJOBABARECRUSTM'][0]['head'][0]['list_total_count']]
        data = response.json()['GGJOBABARECRUSTM'][1]['row']

        for item in data:
            convert_dates_in_dict(item, self.get_date_fields())

        return header, data

    def get_date_fields(self) -> list[str]:
        return ['RCPT_BGNG_DE', 'RCPT_END_DE']

    @property
    def source_name(self) -> str:
        return "GGJobaba"
