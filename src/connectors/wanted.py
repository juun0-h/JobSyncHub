from typing import List, Dict, Any
from .base import BaseConnector
from ..utils.config import Config
from selenium import webdriver
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException
import random
import time

class WantedConnector(BaseConnector):
    """
    Wanted 크롤링, 원티드에서 제공하는 채용 정보를 가져오는 connector class
    """
    def __init__(self):
        self.driver = webdriver.Chrome()
        self.job_links = list() # 공고 별 링크 리스트
        self.job_data = list()  # 수집한 데이터를 저장할 리스트

        self.target_url = Config.WANTEDCONNECTOR_URL                    # 크롤링 할 URL
        self.job_links_html = Config.WANTEDCONNECTOR_GET_LINKS_HTML     # 공고 별 링크 리스트
        self.detail_button_html = Config.WANTEDCONNECTOR_DETAIL_BUTTON_HTML     # 상세 정보 더 보기 버튼
        self.sort_option_html = Config.WANTEDCONNECTOR_PRE_SORT_OPTION_HTML     # 최신순 정렬 버튼

        self.title_html = Config.WANTEDCONNECTOR_TITLE_HTML             # 공고명
        self.company_html = Config.WANTEDCONNECTOR_COMPANY_HTML         # 회사명
        self.location_html = Config.WANTEDCONNECTOR_LOCATION_HTML       # 근무지
        self.deadline_html = Config.WANTEDCONNECTOR_DEADLINE_HTML       # 마감일
        self.experience_html = Config.WANTEDCONNECTOR_EXPERIENCE_HTML   # 경력 사항
        self.skills_html = Config.WANTEDCONNECTOR_SKILLS_HTML           # 기술 스택

    def fetch_data_and_integrate_date_format(self) -> List[Dict[str, Any]]:
        self.job_links = [job.get_attribute('href') for job in self.driver.find_elements(self.job_links_html[0], self.job_links_html[1])]
        cnt = 0

        for index, job_link in enumerate(self.job_links):
            cnt += 1
            print(f"cnt = {cnt}")
            try:
                job_info = {}

                # 링크로 이동
                self.driver.get(job_link)
                self.driver.implicitly_wait(3)  # 페이지 로드 대기

                # 상세 정보 더 보기 버튼 클릭
                try:
                    more_info_button = WebDriverWait(self.driver, 10).until(
                        EC.element_to_be_clickable((self.detail_button_html[0], self.detail_button_html[1]))
                    )
                    more_info_button.click()
                except TimeoutException:
                    print("상세 정보 더보기 버튼 탐색 실패")

                # 공고 링크 추가
                job_info['url'] = job_link

                # 공고명 추출
                try:
                    job_title = self.driver.find_element(self.title_html[0], self.title_html[1]).text
                    job_info['title'] = job_title
                except Exception as e:
                    job_info['title'] = None
                    print(f"[Wanted] title 필드 추출 실패: {job_link}")

                # 회사명 추출
                try:
                    company_name = self.driver.find_element(self.company_html[0], self.company_html[1]).text
                    job_info['company'] = company_name
                except Exception as e:
                    job_info['company'] = None
                    print(f"[Wanted] company 필드 추출 실패: {job_link}")

                # 근무지 추출
                try:
                    location = self.driver.find_element(self.location_html[0], self.location_html[1]).text
                    job_info['location'] = location
                except Exception as e:
                    job_info['location'] = None
                    print(f"[Wanted] location 필드 추출 실패: {job_link}")

                # 마감일 추출
                try:
                    deadline = self.driver.find_element(self.deadline_html[0], self.deadline_html[1]).text
                    job_info['deadline'] = self.convert_deadline_format(deadline, r"\d{4}\.\d{2}\.\d{2}", '%Y.%m.%d')
                except Exception as e:
                    job_info['deadline'] = None
                    print(f"[Wanted] deadline 필드 추출 실패: {job_link}")

                # 경력 사항 추출
                try:
                    experience = self.driver.find_element(self.experience_html[0], self.experience_html[1]).text
                    job_info['experience'] = experience
                except Exception as e:
                    job_info['experience'] = None
                    print(f"[Wanted] experience 필드 추출 실패: {job_link}")

                # 기술 스택 추출
                try:
                    skill_elements = self.driver.find_elements(self.skills_html[0], self.skills_html[1])
                    skills = [skill.text.strip() for skill in skill_elements if skill.text.strip()]
                    job_info['skills'] = skills
                except Exception as e:
                    job_info['skills'] = []
                    print(f"[Wanted] skills 필드 추출 실패: {job_link}")

                # 데이터를 리스트에 추가
                self.job_data.append(job_info)
                print(job_info)

                time.sleep(random.randint(2, 3))

            except Exception as e:
                print(f"에러 발생: {e}")

        # 크롬 드라이버 종료
        self.driver.quit()

        return self.job_data

    def pre_process(self) -> None:
        """
        사전 작업 수행
        chrome driver 실행 및 최신순 정렬 선택
        """
        self.driver.get(self.target_url)
        self.driver.implicitly_wait(5)

        try:
            latest_sort_button = WebDriverWait(self.driver, 10).until(
                EC.element_to_be_clickable((self.sort_option_html[0], self.sort_option_html[1]))
            )
            latest_sort_button.click()
            print("최신순 정렬 선택 완료")
        except TimeoutException:
            print("최신순 정렬 버튼 탐색 실패")

        time.sleep(1)
        self.scroll_to_bottom()

    def scroll_to_bottom(self) -> None:
        """
        페이지를 끝까지 스크롤 다운
        """
        last_height = self.driver.execute_script("return document.body.scrollHeight")
        while True:
            self.driver.execute_script("window.scrollTo(0, document.body.scrollHeight);")
            time.sleep(2)  # 새로운 데이터를 로드할 시간을 대기

            new_height = self.driver.execute_script("return document.body.scrollHeight")
            if new_height == last_height:
                print("페이지 로드 완료")
                break
            last_height = new_height

    def get_date_fields(self) -> List[str]:
        pass

    @property
    def source_name(self) -> str:
        return "Wanted"