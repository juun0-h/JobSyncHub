import unittest
from unittest.mock import patch
from src.connectors.seoul_job_portal import SeoulJobPortalConnector

class TestSeoulJobPortalConnector(unittest.TestCase):
    @patch('src.connectors.seoul_job_portal.requests.get')
    def test_fetch_data(self, mock_get):
        mock_response = mock_get.return_value
        mock_response.status_code = 200
        mock_response.json.return_value = {
            'GetJobInfo': {
                'row': [
                    {
                        'CMPNY_NM': '성심복지센터',
                        'JO_REG_DT': '2024-08-07',
                        'RCEPT_CLOS_NM': '마감일 (2024-10-04)'
                    }
                ]
            }
        }

        connector = SeoulJobPortalConnector()
        data = connector.fetch_data()

        self.assertEqual(len(data), 1)
        self.assertEqual(data[0]['CMPNY_NM'], '성심복지센터')
        self.assertEqual(data[0]['JO_REG_DT'], '2024-08-07')
        self.assertEqual(data[0]['RCEPT_CLOS_NM'], '2024-10-04')

if __name__ == '__main__':
    unittest.main()