import unittest
from unittest.mock import patch
from src.connectors.public_institution import PublicInstitutionConnector

class TestPublicInstitutionConnector(unittest.TestCase):
    @patch('src.connectors.public_institution.requests.get')
    def test_fetch_data(self, mock_get):
        mock_response = mock_get.return_value
        mock_response.status_code = 200
        mock_response.json.return_value = {
            'result': [
                {
                    'instNm': '한국수출입은행',
                    'pbancBgngYmd': '20240819',
                    'pbancEndYmd': '20240903'
                }
            ]
        }

        connector = PublicInstitutionConnector()
        data = connector.fetch_data()

        self.assertEqual(len(data), 1)
        self.assertEqual(data[0]['instNm'], '한국수출입은행')
        self.assertEqual(data[0]['pbancBgngYmd'], '2024-08-19')
        self.assertEqual(data[0]['pbancEndYmd'], '2024-09-03')

if __name__ == '__main__':
    unittest.main()