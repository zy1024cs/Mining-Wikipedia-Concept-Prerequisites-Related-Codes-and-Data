# -*- coding:utf-8 -*-
# Author:Zhou Yang
# Time:2019/3/30



import tagme
import logging
import sys
import os.path

# 标注的“Authorization Token”，需要注册才有
tagme.GCUBE_TOKEN = "xxxxxxx-4b42-4299-a9ef-6f932cfa75af-843339462"

program = os.path.basename(sys.argv[0])
logger = logging.getLogger(program)
logging.basicConfig(format='%(asctime)s: %(levelname)s: %(message)s')




def Annotation_mentions(txt):
    """
    发现那些文本中可以是维基概念实体的概念
    :param txt: 一段文本对象，str类型
    :return: 键值对，键为本文当中原有的实体概念，值为该概念作为维基概念的概念大小，那些属于维基概念但是存在歧义现象的也包含其内
    """
    annotation_mentions = tagme.mentions(txt)
    dic = dict()
    for mention in annotation_mentions.mentions:
        try:
            dic[str(mention).split(" [")[0]] = str(mention).split("] lp=")[1]
        except:
            logger.error('error annotation_mention about ' + mention)
    return dic


def Annotate(txt, language="en", theta=0.1):
    """
    解决文本的概念实体与维基百科概念之间的映射问题
    :param txt: 一段文本对象，str类型
    :param language: 使用的语言 “de”为德语, “en”为英语，“it”为意语.默认为英语“en”
    :param theta:阈值[0, 1]，选择标注得分，阈值越大筛选出来的映射就越可靠，默认为0.1
    :return:键值对[(A, B):score]  A为文本当中的概念实体，B为维基概念实体，score为其得分
    """
    annotations = tagme.annotate(txt, lang=language)
    dic = dict()
    for ann in annotations.get_annotations(theta):
        # print(ann)
        try:
            A, B, score = str(ann).split(" -> ")[0], str(ann).split(" -> ")[1].split(" (score: ")[0], str(ann).split(" -> ")[1].split(" (score: ")[1].split(")")[0]
            dic[(A, B)] = score
        except:
            logger.error('error annotation about ' + ann)
    return dic


if __name__ == '__main__':
    f = open("text.txt", "r", encoding="utf8")
    txt = f.read()
    obj = Annotation_mentions(txt)
    for i in obj.keys():
        print(i + "  " + obj[i])
    print("=" * 30)
    obj = Annotate(txt, theta=0.1)
    for i in obj.keys():
        print(i[0] + " ---> " + i[1] + "  " + obj[i])

    pass









