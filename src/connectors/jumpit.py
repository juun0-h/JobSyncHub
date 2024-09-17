from typing import List, Dict, Any
from .base import BaseConnector
from ..utils.config import Config
from selenium import webdriver
import random
import time

class JumpItConnector(BaseConnector):
    """
    JumpIt 크롤링, 점핏에서 제공하는 채용 정보를 가져오는 connector class
    """
    def __init__(self):
        self.driver = webdriver.Chrome()
        self.job_links = list() # 공고 별 링크 리스트
        self.job_data = list()  # 수집한 데이터를 저장할 리스트

        self.target_url = Config.JUMPITCONNECTOR_URL                # 크롤링 할 URL
        self.job_links_html = Config.JUMPITCONNECTOR_JOB_LINKS_HTML # 공고 별 링크 리스트
        self.title_html = Config.JUMPITCONNECTOR_TITLE_HTML         # 공고명
        self.company_html = Config.JUMPITCONNECTOR_COMPANY_HTML     # 회사명
        self.location_html = Config.JUMPITCONNECTOR_LOCATION_HTML   # 근무지
        self.deadline_html = Config.JUMPITCONNECTOR_DEADLINE_HTML   # 마감일
        self.experience_html = Config.JUMPITCONNECTOR_EXPERIENCE_HTML   # 경력 사항
        self.skills_html = Config.JUMPITCONNECTOR_SKILLS_HTML       # 기술 스택

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

                # 공고 링크 추가
                job_info['url'] = job_link

                # 공고명 추출
                try:
                    job_title = self.driver.find_element(self.title_html[0], self.title_html[1]).text
                    job_info['title'] = job_title
                except:
                    job_info['title'] = None
                    print(f"[JumpIt] title 필드 추출 실패: {job_link}")

                # 회사명 추출
                try:
                    company_name = self.driver.find_element(self.company_html[0], self.company_html[1]).text
                    job_info['company'] = company_name
                except:
                    job_info['company'] = None
                    print(f"[JumpIt] company 필드 추출 실패: {job_link}")

                # 근무지 추출
                try:
                    job_location = self.driver.find_element(self.location_html[0], self.location_html[1]).text
                    job_info['location'] = job_location
                except:
                    job_info['location'] = None
                    print(f"[JumpIt] location 필드 추출 실패: {job_link}")

                # 마감일 추출
                try:
                    deadline = self.driver.find_element(self.deadline_html[0], self.deadline_html[1]).text
                    job_info['deadline'] = self.convert_deadline_format(deadline, r"\d{4}\-\d{2}\-\d{2}", '%Y-%m-%d')
                except:
                    job_info['deadline'] = None
                    print(f"[JumpIt] deadline 필드 추출 실패: {job_link}")

                # 경력 사항 추출
                try:
                    experience = self.driver.find_element(self.experience_html[0], self.experience_html[1]).text
                    job_info['experience'] = experience
                except:
                    job_info['experience'] = None
                    print(f"[JumpIt] experience 필드 추출 실패: {job_link}")

                # 기술 스택 추출
                try:
                    skills_elements = self.driver.find_elements(self.skills_html[0], self.skills_html[1])
                    skills = [skill.text.strip() for skill in skills_elements]
                    job_info['skills'] = skills
                except:
                    job_info['skills'] = []
                    print(f"[JumpIt] skills 필드 추출 실패: {job_link}")

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
        chrome driver를 이용하여 target_url로 이동
        :return:
        """
        self.driver.get(self.target_url)
        self.driver.implicitly_wait(5)

        self.scroll_to_bottom()

    def scroll_to_bottom(self) -> None:
        """
        페이지의 끝까지 스크롤 다운
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
        return "Jumpit"