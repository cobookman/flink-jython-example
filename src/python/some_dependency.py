import requests

def get(url):
    return requests.get(url, headers={'Accept-Encoding': 'defalte'})