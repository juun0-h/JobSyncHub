import unittest
from unittest.mock import patch
from src.connectors.ggjobaba import GGJobabaConnector

class TestGGJobabaConnector(unittest.TestCase):
    @patch('src.connectors.ggjobaba.requests.get')
    def test_fetch_data(self, mock_get):
        # Mock the API response
        mock_response = mock_get.return_value
        mock_response.status_code = 200
        mock_response.json.return_value = {
            'GGJOBABARECRUSTM': [
                {},
                {'row': [
                    {
                        'ENTRPRS_NM': '(주)보성엔지니어링',
                        'RCPT_BGNG_DE': '2024-07-16',
                        'RCPT_END_DE': '2024-07-31'
                    }
                ]}
            ]
        }

        connector = GGJobabaConnector()
        data = connector.fetch_data()

        self.assertEqual(len(data), 1)
        self.assertEqual(data[0]['ENTRPRS_NM'], '(주)보성엔지니어링')
        self.assertEqual(data[0]['RCPT_BGNG_DE'], '2024-07-16')
        self.assertEqual(data[0]['RCPT_END_DE'], '2024-07-31')

if __name__ == '__main__':
    unittest.main()