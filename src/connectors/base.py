from abc import ABC, abstractmethod
from typing import List, Dict, Any, Union
import datetime
import re

class BaseConnector(ABC):
    @abstractmethod
    def fetch_data_and_integrate_date_format(self) -> dict[Any, Any]:
        """
        API에서 header 및 data를 가져오고 날짜 형식 변환 (YYYY-MM-DD)
        
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

    @staticmethod
    def convert_deadline_format(deadline: str, pattern: str, format: str) -> Union[int, str]:
        """
        마감일 형식 변환하는 정적 메소드
        자식 connector에서 공통으로 사용하므로 부모 클래스에 정적 메소드로 구현
        패턴 일치할 경우 Unix timestamp 값으로 변환
        일치하지 않을 경우 변환하지 않고 그대로 반환

        :param deadline: 마감일
        :param pattern: 날짜 형식 패턴
        :param format: 날짜 형식
        :return: 변환된 마감일
        """
        if re.match(pattern, deadline):
            try:
                # 문자열 시작과 끝 공백 제거
                deadline = deadline.strip()
                # 주어진 날짜 문자열을 지정된 형식(format)에 맞춰 datetime 객체로 변환
                dt = datetime.datetime.strptime(deadline, format)
                # 23:59 시간 설정 후 한국 시간으로 변경
                dt = dt.replace(hour=23, minute=59, second=0) + datetime.timedelta(hours=9)
                # unix 타임스탬프로 변환 후 반환
                return int(dt.timestamp())
            except ValueError:
                return deadline
        else:
            # 패턴이 일치하지 않으면 원본 문자열 반환
            return deadline