from abc import ABC, abstractmethod
from typing import List, Dict, Any

class BaseConnector(ABC):
    @abstractmethod
    def fetch_data(self) -> List[Dict[str, Any]]:
        """
        API에서 data를 가져오고 날짜 형식 변환 (YYYY-MM-DD)
        
        Returns:
            API에서 가져온 data list
        """
        pass

    @abstractmethod
    def get_date_fields(self) -> List[str]:
        """
        날짜 형식 변환이 필요한 field 이름 list를 반환
        
        Returns:
            날짜 field 이름 list
        """
        pass

    @property
    @abstractmethod
    def source_name(self) -> str:
        """
        Data source의 이름을 반환하는 property
        
        Returns:
            Data source 이름
        """
        pass