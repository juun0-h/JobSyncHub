import os
from dotenv import load_dotenv
from selenium.webdriver.common.by import By

load_dotenv()

class Config:
    AWS_ACCESS_KEY_ID = os.getenv("AWS_ACCESS_KEY_ID")
    AWS_SECRET_ACCESS_KEY = os.getenv("AWS_SECRET_ACCESS_KEY")
    S3_BUCKET_NAME = os.getenv("S3_BUCKET_NAME")

    SARAMINCONNECTOR_API_KEY = os.getenv("SARAMIN_API_KEY")
    SARAMINCONNECTOR_URL = os.getenv("SARAMIN_URL")

    SEOULJOBPORTALCONNECTOR_API_KEY = os.getenv("SEOUL_API_KEY")
    SEOULJOBPORTALCONNECTOR_URL = os.getenv("SEOUL_URL")

    GGJOBABACONNECTOR_API_KEY = os.getenv("GYEONGGI_API_KEY")
    GGJOBABACONNECTOR_URL = os.getenv("GYEONGGI_URL")
    
    PUBLICINSTITUTIONCONNECTOR_API_KEY = os.getenv("PUBLIC_INSTITUTION_API_KEY")
    PUBLICINSTITUTIONCONNECTOR_URL = os.getenv("PUBLIC_INSTITUTION_URL")


    ### JobPlanet Connector ###
    JOBPLANETCONNECTOR_URL = 'https://www.jobplanet.co.kr/job'
    JOBPLANETCONNECTOR_GET_LINKS_HTML = (By.CSS_SELECTOR, 'div.overflow-hidden.medium a')
    JOBPLANETCONNECTOR_MODAL_IFRAME_HTML = (By.CSS_SELECTOR, "iframe[title='Modal Message']")
    JOBPLANETCONNECTOR_CLOSE_BUTTON_MODAL_HTML = (By.ID, "ijv1")

    JOBPLANETCONNECTOR_PRE_SORT_OPTION_HTML = (By.ID, "location")
    JOBPLANETCONNECTOR_PRE_FIRST_OPTION_HTML = (By.XPATH, "//a[contains(text(), '직종')]")
    JOBPLANETCONNECTOR_PRE_SECOND_OPTION_HTML = (By.XPATH, "//button[contains(text(), '개발')]")
    JOBPLANETCONNECTOR_PRE_THIRD_OPTION_HTML = (By.XPATH, "/html/body/div[1]/main/div[1]/div/div[2]/div[1]/div[1]/div/div[1]/div[2]/ul/li[1]/label/i")
    JOBPLANETCONNECTOR_PRE_FOURTH_OPTION_HTML = (By.XPATH, "//button[contains(text(), '적용')]")

    JOBPLANETCONNECTOR_TITLE_HTML = (By.CSS_SELECTOR, 'h1.ttl')
    JOBPLANETCONNECTOR_COMPANY_HTML = (By.CSS_SELECTOR, 'span.company_name a')
    JOBPLANETCONNECTOR_LOCATION_HTML = (By.CSS_SELECTOR, 'span.job_location .item')
    JOBPLANETCONNECTOR_DEADLINE_HTML = (By.CSS_SELECTOR, '.recruitment-summary__end')
    JOBPLANETCONNECTOR_EXPERIENCE_HTML = (By.XPATH, "//dt[contains(.,'경력')]/following-sibling::dd")
    JOBPLANETCONNECTOR_SKILLS_HTML = (By.XPATH, "//dt[contains(.,'스킬')]/following-sibling::dd")
    #########################

    ### Wanted Connector ###
    WANTEDCONNECTOR_URL = 'https://www.wanted.co.kr/wdlist/518?country=kr&job_sort=job.recommend_order&years=-1&locations=all'
    WANTEDCONNECTOR_GET_LINKS_HTML = (By.XPATH, '//ul[@data-cy="job-list"]/li//a[@data-attribute-id="position__click"]')
    WANTEDCONNECTOR_DETAIL_BUTTON_HTML = (By.XPATH, '//button[@class="Button_Button__root__m1NGq Button_Button__outlined__0HnEd Button_Button__outlinedAssistive__JKDyz Button_Button__outlinedSizeLarge__A_H8o Button_Button__fullWidth__zAnDP"]')

    WANTEDCONNECTOR_PRE_SORT_OPTION_HTML = (By.XPATH, "//span[contains(text(),'최신순')]/parent::button")

    WANTEDCONNECTOR_TITLE_HTML = (By.CSS_SELECTOR, 'h1.JobHeader_JobHeader__PositionName__kfauc')
    WANTEDCONNECTOR_COMPANY_HTML = (By.CSS_SELECTOR, 'a.JobHeader_JobHeader__Tools__Company__Link__zAvYv')
    WANTEDCONNECTOR_LOCATION_HTML = (By.CSS_SELECTOR, 'span.JobHeader_JobHeader__Tools__Company__Info__yT4OD:nth-child(3)')
    WANTEDCONNECTOR_DEADLINE_HTML = (By.CSS_SELECTOR, 'article.JobDueTime_JobDueTime__3yzxa > span')
    WANTEDCONNECTOR_EXPERIENCE_HTML = (By.CSS_SELECTOR, 'span.JobHeader_JobHeader__Tools__Company__Info__yT4OD:nth-child(5)')
    WANTEDCONNECTOR_SKILLS_HTML = (By.XPATH, '//article[@class="JobSkillTags_JobSkillTags__UA0s6"]//ul/li/span')
    #########################

    ### Jumpit Connector ###
    JUMPITCONNECTOR_URL = 'https://www.jumpit.co.kr/positions?sort=reg_dt'
    JUMPITCONNECTOR_JOB_LINKS_HTML = (By.CSS_SELECTOR, 'section.sc-ac9b42ce-0.etGTSP div.sc-d609d44f-0.grDLmW > a')

    JUMPITCONNECTOR_TITLE_HTML = (By.CSS_SELECTOR, 'div.sc-f491c6ef-0.egBfVn > h1')
    JUMPITCONNECTOR_COMPANY_HTML = (By.CSS_SELECTOR, 'a[href*="/company/"] span')
    JUMPITCONNECTOR_LOCATION_HTML = (By.XPATH, "//dt[contains(text(),'근무지역')]/following-sibling::dd//li")
    JUMPITCONNECTOR_DEADLINE_HTML = (By.XPATH, "//dt[contains(text(),'마감일')]/following-sibling::dd")
    JUMPITCONNECTOR_EXPERIENCE_HTML = (By.XPATH, "//dt[contains(text(),'경력')]/following-sibling::dd")
    JUMPITCONNECTOR_SKILLS_HTML = (By.XPATH, "//dt[contains(text(),'기술스택')]/following-sibling::dd//div")
    #########################