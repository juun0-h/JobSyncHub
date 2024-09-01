import unittest
from unittest.mock import patch
from src.connectors.saramin import SaraminConnector

class TestSaraminConnector(unittest.TestCase):
    @patch('src.connectors.saramin.requests.get')
    def test_fetch_data(self, mock_get):
        mock_response = mock_get.return_value
        mock_response.status_code = 200
        mock_response.json.return_value = {
            'jobs': {
                'job': [
                    {
                        'company': {'detail': {'name': '(주)로지시스'}},
                        'opening-timestamp': '1724058000',
                        'expiration-timestamp': '1726671599'
                    }
                ]
            }
        }

        connector = SaraminConnector()
        data = connector.fetch_data()

        self.assertEqual(len(data), 1)
        self.assertEqual(data[0]['company']['detail']['name'], '(주)로지시스')
        self.assertEqual(data[0]['opening-timestamp'], '2024-08-19')
        self.assertEqual(data[0]['expiration-timestamp'], '2024-09-18')

if __name__ == '__main__':
    unittest.main()