from .base import BaseConnector
from ..utils.config import Config
from selenium import webdriver
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException
from selenium.webdriver.support.ui import Select
from typing import List, Dict, Any
import random
import time

class JobPlanetConnector(BaseConnector):
    """
    JobPlanet 크롤링, 잡플래닛에서 제공하는 채용 정보를 가져오는 connector class
    """
    def __init__(self):
        self.driver = webdriver.Chrome()
        self.job_links = list()     # 공고 별 링크 리스트
        self.job_data = dict()      # 수집한 데이터를 저장할 딕셔너리

        self.target_url = Config.JOBPLANETCONNECTOR_URL                     # 크롤링 할 URL
        self.job_links_html = Config.JOBPLANETCONNECTOR_GET_LINKS_HTML      # 공고 리스트
        self.modal_iframe_html = Config.JOBPLANETCONNECTOR_MODAL_IFRAME_HTML        # 모달 iframe
        self.close_button_modal_html = Config.JOBPLANETCONNECTOR_CLOSE_BUTTON_MODAL_HTML    # 모달 닫기 버튼
        self.sort_option_html = Config.JOBPLANETCONNECTOR_PRE_SORT_OPTION_HTML      # 정렬 옵션
        self.first_option_html = Config.JOBPLANETCONNECTOR_PRE_FIRST_OPTION_HTML    # 직종 버튼 (first option)
        self.second_option_html = Config.JOBPLANETCONNECTOR_PRE_SECOND_OPTION_HTML  # 개발 버튼 (second option)
        self.third_option_html = Config.JOBPLANETCONNECTOR_PRE_THIRD_OPTION_HTML    # 개발 전체 체크박스 (third option)
        self.fourth_option_html = Config.JOBPLANETCONNECTOR_PRE_FOURTH_OPTION_HTML  # 적용 버튼 (fourth option)

        self.title_html = Config.JOBPLANETCONNECTOR_TITLE_HTML          # 공고명
        self.company_html = Config.JOBPLANETCONNECTOR_COMPANY_HTML      # 회사명
        self.location_html = Config.JOBPLANETCONNECTOR_LOCATION_HTML    # 근무지
        self.deadline_html = Config.JOBPLANETCONNECTOR_DEADLINE_HTML    # 마감일
        self.experience_html = Config.JOBPLANETCONNECTOR_EXPERIENCE_HTML    # 경력 사항
        self.skills_html = Config.JOBPLANETCONNECTOR_SKILLS_HTML        # 기술 스택

    def fetch_data_and_integrate_date_format(self) -> dict[Any, Any]:
        cnt = 0
        self.job_links = [job.get_attribute('href') for job in self.driver.find_elements(self.job_links_html[0], self.job_links_html[1])]

        for index, job_link in enumerate(self.job_links):
            cnt += 1
            print(f"cnt = {cnt}")
            try:
                job_info = {}

                # 링크로 이동
                self.driver.get(job_link)
                self.driver.implicitly_wait(3)  # 페이지 로드 대기

                # 채용공고 링크 추가
                job_info['url'] = job_link

                # 공고명 추출
                try:
                    job_title = self.driver.find_element(self.title_html[0], self.title_html[1]).text
                    job_info['title'] = job_title
                except:
                    job_info['title'] = None
                    print(f"[JobPlanet] title 필드 추출 실패: {job_link}")

                # 회사명 추출
                try:
                    company_name = self.driver.find_element(self.company_html[0], self.company_html[1]).text
                    job_info['company'] = company_name
                except:
                    job_info['company'] = None
                    print(f"[JobPlanet] company 필드 추출 실패: {job_link}")

                # 근무지 추출
                try:
                    job_location = self.driver.find_element(self.location_html[0], self.location_html[1]).text
                    job_info['location'] = job_location
                except:
                    job_info['location'] = None
                    print(f"[JobPlanet] location 필드 추출 실패: {job_link}")

                # 마감일 추출
                try:
                    deadline = self.driver.find_element(self.deadline_html[0], self.deadline_html[1]).text
                    job_info['deadline'] = self.convert_deadline_format(deadline, r"\d{4}\.\d{2}\.\d{2}", '%Y.%m.%d')
                except:
                    job_info['deadline'] = None
                    print(f"[JobPlanet] deadline 필드 추출 실패: {job_link}")

                # 경력 사항 추출
                try:
                    experience = self.driver.find_element(self.experience_html[0], self.experience_html[1]).text
                    job_info['experience'] = experience
                except:
                    job_info['experience'] = None
                    print(f"[JobPlanet] experience 필드 추출 실패: {job_link}")

                # 기술 스택 추출
                try:
                    skills_elements = self.driver.find_elements(self.skills_html[0], self.skills_html[1])
                    combi_skills = ", ".join([skill.text.strip() for skill in skills_elements])
                    # 쉼표로 분리하여 리스트로 변환
                    skills = [s.strip() for s in combi_skills.split(',')]

                    job_info['skills'] = skills
                except:
                    job_info['skills'] = []
                    print(f"[JobPlanet] skills 필드 추출 실패: {job_link}")

                # 데이터를 딕셔너리에 추가
                self.job_data[cnt] = job_info
                print(job_info)

                time.sleep(random.randint(2, 3))

            except Exception as e:
                print(f"에러 발생: {e}")

        # 크롬 드라이버 종료
        self.driver.quit()

        return self.job_data

    def pre_process(self) -> None:
        """
        사전 작업을 수행
        chrome driver 실행
        모달이 뜰 경우 닫기
        직종 -> 개발 -> 개발 전체 -> 적용 -> 최신순 정렬
        :return:
        """
        self.driver.get(self.target_url)
        time.sleep(3)

        # 모달 닫기
        self.close_modal()
        time.sleep(3)

        # '직종' 드롭다운 메뉴 클릭
        try:
            job_type_button = WebDriverWait(self.driver, 10).until(
                EC.element_to_be_clickable((self.first_option_html[0], self.first_option_html[1]))  # '직종' 버튼을 클릭
            )
            job_type_button.click()
            print("직종 메뉴 클릭 완료")
        except TimeoutException:
            print("직종 메뉴 탐색 실패")

        # '개발' 버튼 클릭
        try:
            development_button = WebDriverWait(self.driver, 10).until(
                EC.element_to_be_clickable((self.second_option_html[0], self.second_option_html[1]))  # '개발' 버튼 클릭
            )
            development_button.click()
            print("개발 선택 완료")
        except TimeoutException:
            print("개발 버튼 탐색 실패")

        # '개발 전체' 체크 박스 클릭
        try:
            development_all_checkbox = WebDriverWait(self.driver, 10).until(
                EC.element_to_be_clickable((self.third_option_html[0], self.third_option_html[1]))
            )
            development_all_checkbox.click()
            print("개발 전체 선택 완료")
        except TimeoutException:
            print("개발 전체 체크박스 탐색 실패")

        # '적용' 버튼 클릭
        try:
            apply_button = WebDriverWait(self.driver, 10).until(
                EC.element_to_be_clickable((self.fourth_option_html[0], self.fourth_option_html[1]))  # '적용' 버튼 클릭
            )
            apply_button.click()
            print("적용 버튼 클릭 완료")
        except TimeoutException:
            print("적용 버튼 탐색 실패")

        # 최신순 정렬
        try:
            dropdown = WebDriverWait(self.driver, 10).until(
                EC.presence_of_element_located((self.sort_option_html[0], self.sort_option_html[1]))
            )
            select = Select(dropdown)
            select.select_by_value("recent")  # 최신순 선택
            print("최신순 정렬 선택 완료")
        except TimeoutException:
            print("최신순 정렬 옵션 탐색 실패")

        # 필터 적용 후 페이지 스크롤 다운
        time.sleep(2)
        self.scroll_to_bottom()

    def close_modal(self) -> None:
        """
        모달을 닫는 함수
        """
        try:
            # iframe으로 전환
            WebDriverWait(self.driver, 10).until(
                EC.frame_to_be_available_and_switch_to_it((self.modal_iframe_html[0], self.modal_iframe_html[1])))

            # '닫기' 버튼 클릭 -> 요거 자주 바뀜
            close_button = WebDriverWait(self.driver, 10).until(
                EC.element_to_be_clickable((self.close_button_modal_html[0], self.close_button_modal_html[1]))
            )
            close_button.click()
            print("모달이 닫힘")

            # iframe에서 기존 컨텐츠로 다시 전환
            self.driver.switch_to.default_content()
            time.sleep(1)

        except TimeoutException:
            print("모달 탐색 실패")

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

    def get_date_fields(self) -> list[str]:
        pass

    @property
    def source_name(self) -> str:
        return "JobPlanet"