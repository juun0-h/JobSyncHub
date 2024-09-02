import requests
from .base import BaseConnector
from ..utils.config import Config
from typing import List, Dict, Any
from ..utils.helpers import convert_dates_in_dict

class PublicInstitutionConnector(BaseConnector):
    """
    Public Institution API, 기획재정부에서 제공하는 공공기관 채용 정보를 가져오는 connector class
    """

    def __init__(self):
        """
        Attributes:
            api_key (str): Public Institution API key
            base_url (str): Public Institution API의 기본 URL
        """
        self.api_key = Config.PUBLICINSTITUTIONCONNECTOR_API_KEY
        self.base_url = Config.PUBLICINSTITUTIONCONNECTOR_URL

    def fetch_data(self) -> List[Dict[str, Any]]:
        params = {
            'serviceKey': self.api_key,
            'type': 'json',
            'numOfRows': '1000',
            # 'pageNo': '1'
        }
        response = requests.get(self.base_url, params=params)
        response.raise_for_status()
        data = response.json()['result']

        # tmp for testing total count
        print(f"totalCount: {response.json()['totalCount']}")


        for item in data:
            convert_dates_in_dict(item, self.get_date_fields())

        return data
    
    def get_date_fields(self) -> List[str]:
        return ['pbancBgngYmd', 'pbancEndYmd']
    
    @property
    def source_name(self) -> str:
        return "PublicInstitution"