from pydantic import BaseModel
from typing import Optional
from pendulum import datetime, date

class JobPosting(BaseModel):
    id: str
    company_name: str
    job_title: str
    location: str
    experience_level: str
    education_level: str
    salary: Optional[str]
    application_start_date: datetime
    application_end_date: datetime
    number_of_positions: Optional[int]
    job_category: str
    employment_type: str
    job_description: Optional[str]
    expired: bool
    url: Optional[str]
    source: str