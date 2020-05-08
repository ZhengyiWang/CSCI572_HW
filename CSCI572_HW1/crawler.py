from bs4 import BeautifulSoup
import time
import requests
from random import randint
import json
from html.parser import HTMLParser

USER_AGENT = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) '
                            'Chrome/61.0.3163.100 Safari/537.36'}


class SearchEngine:
    @staticmethod
    def search(query, sleep=True):
        if sleep:  # Prevents loading too many pages too soon
            time.sleep(randint(10, 100))
            temp_url = '+'.join(query.split())  # for adding + between words for the query
            url = 'https://www.teoma.com/web?q=' + temp_url
            soup = BeautifulSoup(requests.get(url, headers=USER_AGENT).text, "html.parser")
            new_results = SearchEngine.scrape_search_result(soup)

        return new_results

    @staticmethod
    def scrape_search_result(soup):
        raw_results = soup.find_all("a", attrs = {"class" : "algo-title"})
        results = []
        count = 0
        # implement a check to get only 10 results and also check that URLs must not be duplicated
        for r in raw_results:
            if count != 10:
                link = r.find('a').get('href')
                if link not in results:
                    results.append(link)
                    count += 1
        print(results)
        return results


# f = open("queries.txt", "r")
# total = f.readlines()
# result = SearchEngine.search(total[])
# print(result)
# print(len(result))

f = open("queries.txt", "r")
result = {}
for i in f:
    result[i] = SearchEngine.search(i)
with open('result.txt', 'w') as json_file:
    json.dump(result, json_file)