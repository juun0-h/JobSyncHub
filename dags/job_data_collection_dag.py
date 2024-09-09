from airflow import DAG
from airflow.decorators import task
from datetime import timedelta
import pendulum
from src.connectors.ggjobaba import GGJobabaConnector
from src.connectors.seoul_job_portal import SeoulJobPortalConnector
from src.connectors.public_institution import PublicInstitutionConnector
from src.connectors.saramin import SaraminConnector
from src.utils.s3_utils import upload_to_s3
from src.utils.config import Config

# 한국 시간대 설정
KST = pendulum.timezone("Asia/Seoul")

default_args = {
    'owner': 'airflow',
    'depends_on_past': False,
    'email_on_failure': False,
    'email_on_retry': False,
    'retries': 1,
    'retry_delay': timedelta(minutes=5),
}

@task
def fetch_and_upload_data(connector_class):
    connector = connector_class()
    data = connector.fetch_data()
    date_str = pendulum.now(KST).format('YYYY-MM-DD HH:mm:ss')
    filename = f"{connector_class.__name__}/{date_str}.json"
    
    config = {
        "api_key": getattr(Config, f"{connector_class.__name__.upper()}_API_KEY"),
        "url": getattr(Config, f"{connector_class.__name__.upper()}_URL"),
        "source": connector_class.__name__,
        "last_updated": date_str
    }

    
    upload_to_s3(data, config, filename)

with DAG(
    'saramin_data_collection',
    default_args=default_args,
    description='Collect job data from Saramin (Mon-Fri, every hour from 9 AM to midnight KST)',
    schedule_interval=pendulum.cron('0 9-23 * * 1-5', tz=KST),
    start_date=pendulum.datetime(2024, 9, 1, tz=KST),
    catchup=True,
) as saramin_dag:

    fetch_and_upload_data.override(task_id='fetch_and_upload_Saramin')(SaraminConnector)

# 월-금 오후 6시 (한국 시간)에 실행되는 DAG
with DAG(
    'job_data_collection_weekday',
    default_args=default_args,
    description='Collect job data from Seoul and Public Institution (Mon-Fri at 6 PM KST)',
    schedule_interval=pendulum.cron('0 18 * * 1-5', tz=KST),
    start_date=pendulum.datetime(2024, 9, 1, tz=KST),
    catchup=True,
) as weekday_dag:

    weekday_connectors = [
        SeoulJobPortalConnector,
        PublicInstitutionConnector
    ]

    for connector_class in weekday_connectors:
        fetch_and_upload_data.override(task_id=f'fetch_and_upload_{connector_class.__name__}')(connector_class)

# 매주 금요일 오후 6시 (한국 시간)에 실행되는 DAG
with DAG(
    'job_data_collection_friday',
    default_args=default_args,
    description='Collect job data from GGJobaba (Every Friday at 6 PM KST)',
    schedule_interval=pendulum.cron('0 18 * * 5', tz=KST),
    start_date=pendulum.datetime(2024, 9, 1, tz=KST),
    catchup=True,
) as friday_dag:

    fetch_and_upload_data.override(task_id='fetch_and_upload_GGJobaba')(GGJobabaConnector)