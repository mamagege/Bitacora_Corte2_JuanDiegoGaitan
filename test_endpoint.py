import urllib.request
import ssl
import json
import urllib.error

url = 'https://localhost:8443/api/v1/platos'
headers = {
    'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnRlQHJlc3RhdXJhbnRlLmNvbSIsInJvbCI6IlJPTEVfQ0xJRU5URSIsImlhdCI6MTc5MTM1NjgxNCwiZXhwIjoxNzkxMzYwNDE0fQ.8PWh8vi7XLTKAJAJh2zczCMmJXpBYX3U2trjyHn-j6E',
    'Content-Type': 'application/json'
}
data = json.dumps({"nombre": "string"}).encode('utf-8')

ctx = ssl.create_default_context()
ctx.check_hostname = False
ctx.verify_mode = ssl.CERT_NONE

req = urllib.request.Request(url, data=data, headers=headers, method='POST')

try:
    response = urllib.request.urlopen(req, context=ctx)
    print(response.getcode())
    print(response.read().decode('utf-8'))
except urllib.error.HTTPError as e:
    print(e.code)
    print(e.read().decode('utf-8'))
except Exception as e:
    print(str(e))
