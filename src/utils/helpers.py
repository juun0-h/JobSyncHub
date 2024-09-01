import pendulum
from typing import List, Dict, Any
import re

def convert_date_format(date_str: str, 
                        input_formats: List[str] = ['YYYYMMDD', 'YYYY-MM-DD', 'YYYY.MM.DD'], 
                        output_format: str = 'YYYY-MM-DD') -> str:
    """
    날짜 문자열을 다양한 형식에서 지정된 출력 형식으로 변환

    ------
    Params:
        date_str (str): 변환할 날짜 문자열.
        input_formats (List[str]): 시도할 입력 날짜 형식의 list (default: ['YYYYMMDD', 'YYYY-MM-DD', 'YYYY.MM.DD'])
        output_format (str): 출력할 날짜 형식 (default: 'YYYY-MM-DD')

    -------
    Returns:
        str: 변환된 날짜 문자열 또는 변환 실패 시 원본 문자열
    
    ----------
    Exceptions:
        pendulum.parsing.exceptions.ParserError: 입력 형식이 잘못된 경우
    """
    # 이미 'YYYY-MM-DD' 형식인 경우
    if re.match(r'\d{4}-\d{2}-\d{2}', date_str):
        return date_str
    # 'YYYYMMDD' 형식인 경우
    elif re.match(r'\d{8}', date_str):
        return pendulum.from_format(date_str, 'YYYYMMDD').format(output_format)
    # 다른 형식인 경우
    else:
        for fmt in input_formats:
            try:
                date_obj = pendulum.from_format(date_str, fmt)
                return date_obj.format(output_format)
            except pendulum.parsing.exceptions.ParserError:
                continue    
    
        return date_str

def convert_dates_in_dict(item: Dict[str, Any], date_fields: List[str]) -> Dict[str, Any]:
    """
    주어진 dict에서 날짜 field 변환

    Dict에서 지정된 날짜 field들을 찾아 `convert_date_format`를
    사용하여 형식 변환

    ------
    Params:
        item (Dict[str, Any]): 날짜 field를 포함할 수 있는 data dict
        date_fields (List[str]): 변환할 날짜 field의 이름 list

    -------
    Returns:
        Dict[str, Any]: 날짜 필드가 변환된 딕셔너리.
    """
    for field in date_fields:
        if field in item and item[field]:
            item[field] = convert_date_format(item[field])
    return item
