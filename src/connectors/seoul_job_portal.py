import requests, re
from .base import BaseConnector
from ..utils.config import Config
from ..utils.helpers import convert_dates_in_dict, convert_date_format

class SeoulJobPortalConnector(BaseConnector):
    """
    Seoul Job Portal API, 서울시에서 제공하는 채용 정보를 가져오는 connector class
    """

    def __init__(self):
        """
        Attributes:
            api_key (str): Seoul Job Portal API key
            base_url (str): Seoul Job Portal API의 기본 URL
        """
        self.api_key = Config.SEOULJOBPORTALCONNECTOR_API_KEY
        self.base_url = Config.SEOULJOBPORTALCONNECTOR_URL
    
    def fetch_data_and_integrate_date_format(self) -> tuple[list[int], list[dict[str, any]]]:
        params = {
            'KEY': self.api_key,
            'TYPE': 'json',
            'SERVICE': 'GetJobInfo',
            'START_INDEX': 1,
            'END_INDEX': 1000
        }
        # response = requests.get(self.base_url, params=params)
        url = f"{self.base_url}/{self.api_key}/json/GetJobInfo/{params['START_INDEX']}/{params['END_INDEX']}/"
        response = requests.get(url)
        response.raise_for_status()

        header = [response.json()['GetJobInfo']['list_total_count']]
        data = response.json()['GetJobInfo']['row']

        for item in data:
            convert_dates_in_dict(item, ['JO_REG_DT'])
            if 'RCEPT_CLOS_NM' in item:
                item['RCEPT_CLOS_NM'] = self.extract_date(item['RCEPT_CLOS_NM'])
            item['URL'] = self.make_url(item['JO_REQST_NO'])    # 채용공고 상세 페이지 URL 추가

        return header, data
    
    def extract_date(self, date_str: str) -> str:
        """
        'RCEPT_CLOS_NM' field에서 형식에 맞는 날짜 추출

        ------
        Params:
            date_str (str): 날짜 문자열이 포함된 문자열
        
        -------
        returns:
            str: 추출된 날짜 문자열 또는 원본 문자열
        """
        match = re.search(r'\((\d{4}-\d{2}-\d{2})\)', date_str)
        if match:
            return match.group(1)
        return date_str

    def make_url(self, id: str) -> str:
        """
        서울시 채용정보 상세 페이지 URL 생성

        ------
        Params:
            id (str): 채용공고 고유 ID(JO_REQST_NO)

        -------
        returns:
            str: 채용공고 상세 페이지 URL
        """
        prefix = 'https://job.seoul.go.kr/www/job_offer_info/JobOfferInfo.do?method=selectJobOfferInfoView&joReqstNo='
        return f"{prefix}{id}"

    def get_date_fields(self) -> list[str]:
        return ['JO_REG_DT', 'RCEPT_CLOS_NM']
    
    @property
    def source_name(self) -> str:
        return "SeoulJobPortal"