# -*- coding:utf-8 -*-
# Author:Zhou Yang








import requests
import json

agreement = 'https://'
language = 'en'
organization = '.wikipedia.org/w/api.php'

API_URL = agreement + language + organization




def pageid(title = None, np = 0):
    global API_URL
    URL = API_URL
    if np == 0:
        query_params = {
            'action': 'query',
            'prop': 'info',
            'format': 'json',
            'titles': title
        }
    else:
        query_params = {
            'action': 'query',
            'prop': 'categoryinfo',
            'format': 'json',
            'titles': 'Category:' + title
        }
    try:
        r = requests.get(URL, params=query_params)
        r.raise_for_status()
        r.encoding = 'gb2312'
        html = r.text
    except:
        html = ""
    if html == "":
        return -1
    else:
        try:
            text = json.loads(html, encoding='gb2312')
        except json.JSONDecodeError:
            return -1
        try:
            for i in text["query"]['pages']:
                return int(i)
        except:
            return -1

def summary(title = None):
    global API_URL
    URL = API_URL
    query_params = {
        'action': 'query',
        'prop': 'extracts',
        'explaintext': '',
        'exintro': '',
        'format': 'json',
        'titles': title
    }
    try:
        r = requests.get(URL, params=query_params)
        r.raise_for_status()
        r.encoding = 'gb2312'
        html = r.text
    except:
        print('summary---' + title)
        return ""
    text = json.loads(html, encoding='gb2312')
    id = pageid(title)
    if id != -1:
        try:
            return text["query"]["pages"][str(id)]["extract"]
        except:
            return ""
    else:
        return ""

def links(title = None):
    global API_URL
    URL = API_URL
    query_params = {
        'action': 'query',
        'prop': 'links',
        'pllimit': 'max',
        'plnamespace': '0',
        'format': 'json',
        'titles': title
    }
    try:
        r = requests.get(URL, params=query_params)
        r.raise_for_status()
        r.encoding = 'gb2312'
        html = r.text
    except:
        print('links---' + title)
        return list()
    text = json.loads(html, encoding='gb2312')
    id = pageid(title)
    link = list()
    if id != -1:
        summ = summary(title)
        try:
            for obj in text["query"]['pages'][str(id)]["links"]:
                if obj['title'] in summ or obj['title'].lower() in summ:
                    link.append(obj['title'])
        except:
            return link
    return link

def backlinks(title = None):
    global API_URL
    URL = API_URL
    query_params = {
        'action': 'query',
        'list': 'backlinks',
        'bllimit': 'max',
        'blnamespace': '0',
        'format': 'json',
        'bltitle': title
    }
    try:
        r = requests.get(URL, params=query_params)
        r.raise_for_status()
        r.encoding = 'gb2312'
        html = r.text
    except:
        print('backlinks---' + title)
        return list()
    text = json.loads(html, encoding='gb2312')
    id = pageid(title)
    link = list()
    if id != -1:
        try:
            link = [obj['title'] for obj in text["query"]["backlinks"]]
        except:
            return link
    return link



def categories(title = None):
    global API_URL
    URL = API_URL
    query_params = {
        'action': 'query',
        'prop': 'categories',
        'cllimit': 'max',
        'clshow': '!hidden',
        'format': 'json',
        'clcategories': '',
        'titles': title
    }
    try:
        r = requests.get(URL, params=query_params)
        r.raise_for_status()
        r.encoding = 'gb2312'
        html = r.text
    except:
        print('categories---' + title)
        return list()
    text = json.loads(html, encoding='gb2312')
    id = pageid(title)
    category = set()
    if id != -1:
        try:
            category = [obj['title'][9:] for obj in text["query"]['pages'][str(id)]["categories"]]
        except:
            return category
    return category



def redirects(title=None):
    global API_URL
    URL = API_URL
    query_params = {
        'action': 'query',
        'prop': 'redirects',
        'rdlimit': 'max',
        'format': 'json',
        'titles': title
    }
    try:
        r = requests.get(URL, params=query_params)
        r.raise_for_status()
        r.encoding = 'gb2312'
        html = r.text
    except:
        print('redirects---' + title)
        return list()
    text = json.loads(html, encoding='gb2312')
    id = pageid(title)
    redirect = list()
    if id != -1:
        try:
            redirect = [obj['title'] for obj in text["query"]['pages'][str(id)]["redirects"]]
        except:
            return redirect
    return redirect


def subcats(title=None):
    global API_URL
    URL = API_URL
    query_params = {
        'action': 'query',
        'list': 'categorymembers',
        'cmtype': 'subcat',
        'cmlimit': 'max',
        'format': 'json',
        'cmtitle': 'Category:' + title
    }
    try:
        r = requests.get(URL, params=query_params)
        r.raise_for_status()
        r.encoding = 'gb2312'
        html = r.text
    except:
        print('subcats---' + title)
        return list()
    text = json.loads(html, encoding='gb2312')
    id = pageid(title, np=14)
    subcat = list()
    if id != -1:
        try:
            subcat = [obj['title'][9:] for obj in text["query"]['categorymembers']]
        except:
            return subcat
    return subcat

def supercats(title=None):
    global API_URL
    URL = API_URL
    query_params = {
        'action': 'query',
        'prop': 'categories',
        'cllimit': 'max',
        'format': 'json',
        'clshow': '!hidden',
        'titles': 'Category:' + title
    }
    try:
        r = requests.get(URL, params=query_params)
        r.raise_for_status()
        r.encoding = 'gb2312'
        html = r.text
    except:
        print('supercats---' + title)
        return list()
    text = json.loads(html, encoding='gb2312')
    id = pageid(title, np=14)
    supercat = list()
    if id != -1:
        try:
            supercat = [obj['title'][9:] for obj in text["query"]['pages'][str(id)]["categories"]]
        except:
            return supercat
    return supercat



if __name__ == '__main__':
    title = 'Treaties of Mauritius'
    # print(summary(title))
    # link = links(title)
    # for i in link:
    #     print(i)
    # print(len(links(title)))
    print(subcats(title))

