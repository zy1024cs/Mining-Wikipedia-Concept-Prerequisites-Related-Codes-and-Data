# -*- coding:utf-8 -*-







import requests
import json
import logging
import sys
import os.path
import timeit

agreement = 'https://'
language = 'en'
organization = '.wikipedia.org/w/api.php'

API_URL = agreement + language + organization


program = os.path.basename(sys.argv[0])
logger = logging.getLogger(program)
logging.basicConfig(format='%(asctime)s: %(levelname)s: %(message)s')




def timeuse(mss):
    """
    时间测试
    :param mss: 测试对象
    :return: 无
    """
    print(mss+":\nThe use of time is={0:.6f}s\n".format(timeit.default_timer() - start))
    # start = timeit.default_timer()


def pageid(title = None, np = 0):
    global API_URL
    URL = API_URL
    query_params = {
        'action': 'query',
        'prop': 'info',
        'format': 'json',
        'titles': title
    }
    if np != 0:
        query_params['titles'] = 'Category:' + title
    try:
        r = requests.get(URL, params=query_params)
        r.raise_for_status()
        html, r.encoding = r.text, 'gb2312'
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


def linkss(title = None):
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
        html, r.encoding = r.text, 'gb2312'
    except:
        logger.error('error linkss about ' + title)
        return list()
    text = json.loads(html, encoding='gb2312')
    id = list(text["query"]["pages"].keys())[0]
    link = list()
    try:
        for obj in text["query"]['pages'][id]["links"]:
            link.append(obj['title'])
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
        html, r.encoding = r.text, 'gb2312'
    except:
        logger.error('error categories about ' + title)
        return list()
    text = json.loads(html, encoding='gb2312')
    id = list(text["query"]["pages"].keys())[0]
    category = set()
    if id != -1:
        try:
            category = [obj['title'][9:] for obj in text["query"]['pages'][id]["categories"]]
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
        html, r.encoding = r.text, 'gb2312'
    except:
        logger.error('error redirects about ' + title)
        return list()
    text = json.loads(html, encoding='gb2312')
    id = list(text["query"]["pages"].keys())[0]
    redirect = list()
    if id != -1:
        try:
            redirect = [obj['title'] for obj in text["query"]['pages'][id]["redirects"]]
        except:
            return redirect
    return redirect


def getKeywordURLResult(keyword):
    """
    获得对于关键词的URL页面的json的数据格式
    :param keyword: 关键词
    :return: 关键词对应的URL页面的json格式
    """
    url = 'https://en.wikipedia.org/w/api.php?action=query&titles=' + keyword + \
          '&prop=revisions&rvprop=content&format=json&formatversion=2'     # 仅支持英文
    try:
        r = requests.get(url, timeout=30)
        r.raise_for_status()
        r.encoding = 'utf-8'
        return r.text
    except:
        return "Network Error !!!"
    pass

def get_text_title_content(json_doc):
    """
    获取概述部分和内容部分
    :param json_doc: json格式的URL文本
    :return: 概述文本，内容文本
    """
    quotation_mark = json_doc.find('\'\'\'')
    eqaul_mark = json_doc.find('==')
    if quotation_mark == -1 or eqaul_mark == -1 or quotation_mark >= eqaul_mark:
        return '',''
    title = json_doc[quotation_mark:eqaul_mark]
    content = json_doc[eqaul_mark:]
    return title, content

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
        html, r.encoding = r.text, 'gb2312'
    except:
        logger.error('error summary about ' + title)
        return ""
    text = json.loads(html, encoding='gb2312')
    id = list(text["query"]["pages"].keys())[0]
    try:
        return text["query"]["pages"][id]["extract"]
    except:
        return ""


def getKeywordResult(keyword):
    try:
        r = requests.get(keyword, timeout=30)
        r.raise_for_status()
        r.encoding = 'utf-8'
        return r.text
    except:
        return "error"

def tf(c, obj):
    """
    计算tf(c,obj)
    :param c: 词条c
    :param A: 词条A
    :return: 为一个整数，如果大于等于零代表对应的关键词数，小于零代表出错
    """
    keyword = obj
    url = 'https://en.wikipedia.org/w/api.php?action=query&titles=' + keyword + \
          '&prop=revisions&rvprop=content&format=jsonfm&formatversion=2'
    html = getKeywordResult(url)
    if html=='error':
        return 0
    else:
        sum = html.count('[['+c+']]')
        lists=redirects(c)
        if len(lists)==0:
            pass
        else:
            for i in lists:
                sum+=html.count('[['+i+']]')
        return sum
    pass


from pyrwr.rwr import RWR


from pyrwr.pagerank import PageRank

def rwr(seeds):
    dic = {2010784: 'Ivor Grattan-Guinness', 286788: 'Galilean invariance', 59958: 'Power series', 6513985: 'Mass in general relativity', 266950: "Galileo's ship", 275603: 'Frank Wilczek', 28305: 'String theory', 991: 'Absolute value', 295183: 'Galilean transformation', 33445862: 'Center of momentum frame', 61335: 'Differentiation', 644550: 'Higgs mechanism', 1410595: 'Field equation', 308: 'Aristotle', 18947: 'Metre', 14627: 'Isaac Newton', 26884: 'Superconductivity', 38318895: 'Bra-ket notation', 8429: 'Density', 857235: 'Equivalence principle', 244611: "Newton's law of universal gravitation", 25098: 'Phase velocity', 711862: 'Reissner–Nordström metric', 339024: 'Length contraction', 17553: "Kepler's laws of planetary motion", 288123: 'Worcester Polytechnic Institute', 531104: 'Gravity Probe B', 82728: 'Quantum superposition', 494418: 'Proper time', 12024: 'General relativity', 56683807: 'Shapiro delay', 10902: 'Force', 169552: 'Fifth force', 5961: 'Cognitive psychology', 30876419: 'Quantum state', 2053742: 'Contact force', 25525: 'René Descartes', 34753458: "Gauss' law for gravity", 610202: 'Fine structure', 60828: 'Lepton', 256662: 'Terminal velocity', 11051917: 'Günter Nimtz', 172466: 'Jean Buridan', 51079: 'Magnet', 9604: 'Many-worlds interpretation', 3406142: 'Relativity of simultaneity', 327127: 'John Archibald Wheeler', 76954: 'Parallel', 30400: 'Torque', 26764: 'International System of Units', 19555586: 'Classical mechanics', 74263: 'Frame of reference', 27808: 'Samuel Pepys', 9649: 'Energy', 149861: 'Work (physics)', 23536538: 'University of Wisconsin–Madison', 28736: 'Speed of light', 54386990: 'Absolute time and space', 5826: 'Complex number', 9723174: 'Irwin I. Shapiro', 7954422: 'Cornell University', 48781: 'Philosophiæ Naturalis Principia Mathematica', 495065: 'Louis Essen', 27709: 'Semiconductor', 25219: 'Quanta', 25202: 'Quantum mechanics', 22393: 'Organelle', 37892: 'Thrust', 5155121: 'Richard S. Westfall', 17902: 'Leonhard Euler', 2274: 'Arthur Eddington', 29688374: 'Galileo Galilei', 36001020: 'Gravitational wave detector', 1686861: 'Aether drag hypothesis', 12286: 'Great Plague of London', 5176: 'Calculus', 189951: 'Force carrier', 24577221: 'De Historia piscium', 191734: 'Godfrey Kneller', 55212: "Newton's laws of motion", 19593829: 'Spin (physics)', 174593: 'Socinianism', 1979961: "Torricelli's equation", 276582: 'Ricci curvature', 176931: 'Internet Archive', 6956: 'Conservation law', 174396: 'Bohr radius', 10779: 'Frequency', 561845: 'Jacob Bekenstein', 51624: 'Thomas Young (scientist)', 1135324: 'Conjugate variables', 606874: 'Einstein–Cartan theory', 719601: 'MIT Press', 187408: 'Self-adjoint operator', 154739: 'Electrodeposition', 312881: 'Action (physics)', 33931: 'Weight', 458558: 'List of scientific laws named after people', 426219: 'Classical electromagnetism', 22939: 'Physics', 26998617: 'Field (physics)', 25453985: 'Atomic clock', 2434383: 'Local reference frame', 4795569: 'Hypotheses non fingo', 28748: 'Speed', 37750448: 'Taub-NUT vacuum', 643769: 'Quantum tunnelling', 34083818: 'Entailment', 145343: 'Wave function', 9505941: 'List of quantum-mechanical systems with analytical solutions', 19694: 'Mercury (planet)', 21210: 'Niels Bohr', 239290: 'John Wallis', 10890: 'Fundamental interaction', 72540: 'Newton (unit)', 3691915: '0 (number)', 32533: 'Euclidean vector', 45489756: 'Gravitation', 1251967: 'Princeton University Press', 49210145: 'Coriolis effect', 47641: 'Standard Model', 579929: 'Rest (physics)', 15314901: 'Proper velocity', 33692268: 'List of topics named after Leonhard Euler', 26998547: 'Degrees of freedom (physics and chemistry)', 22222481: "Euler's laws of motion", 2217599: 'Circular symmetry', 1912367: 'Electromagnetic tensor', 204680: 'Four-momentum', 74327: 'Principle of relativity', 1072289: 'Uniform circular motion', 1949268: 'Aether (classical element)', 230488: 'Minkowski space', 30731: 'Teleological argument', 852089: 'Gravitational time dilation', 297839: 'Time dilation', 2288549: 'Momentum operator', 3071186: 'Gravitational acceleration', 26962: 'Special relativity', 22308: 'Oxford', 150159: "Noether's theorem", 19265670: 'Centrifugal force', 2218355: 'Astronomia nova', 2434557: 'Non-inertial reference frame', 14838: 'Inertial frame of reference', 475008: 'Stiffness', 1968588: 'Hypervelocity', 49720: 'Robert Hooke', 182727: "Mach's principle", 2916607: 'Force field (physics)', 265006: 'Dialogue Concerning the Two Chief World Systems', 473514: 'Generalized coordinates', 31429: 'Twin paradox', 9228: 'Earth', 226841: 'Four-acceleration', 2443: 'Acceleration', 12320384: 'Theory of impetus', 474962: 'Translation (physics)', 67088: 'Conservation of energy', 1111581: 'Reaction (physics)', 10886039: 'SunPower', 47922: 'Determinism', 25267: 'Quantum field theory', 20431: 'Momentum', 2413037: 'Isolated system', 240123: 'Plasticity (physics)', 308815: 'Connection (mathematics)', 19555: 'Molecule', 19048: 'Mass', 18404: 'Lorentz transformation', 2839: 'Angular momentum', 11132342: 'Bodmer Library', 33306: 'Water', 455769: 'Rigid body', 5390: 'Conversion of units', 157550: 'Karl Schwarzschild', 25312: 'Quantum gravity', 20491903: 'Velocity', 19737: "Maxwell's equations", 15221133: 'Euler force', 593693: 'Point (geometry)', 211922: 'Impulse (physics)', 18993869: 'Gas', 1209823: 'Rotating reference frame', 17939: 'Light', 692093: 'Oxford University Museum of Natural History', 30214333: '1st Earl of Halifax', 19194778: 'Deformation (mechanics)', 41026: 'Dielectric', 415513: 'Net force', 18589032: 'Particle accelerator', 36269934: 'Geodesic (general relativity)', 28486339: 'Georgia Institute of Technology', 13989702: 'Wolfram Demonstrations Project', 102338: 'Henry Cavendish', 1911511: 'Lorentz scalar', 1025272: 'Bohr–Einstein debates', 946273: 'Harry Ransom Center', 9532: 'Electromagnetism', 5918: 'Continuum mechanics', 20580: 'Motion (physics)', 14909: 'Inertia', 17384910: 'Observer (special relativity)', 70671: 'Stress–energy tensor', 17730: 'Latin', 48991805: 'Contributors to general relativity', 323592: 'Nicolaus Copernicus', 226829: 'Four-velocity', 33130: 'Werner Heisenberg', 38293253: 'Geodesic deviation equation', 19559: 'Mechanics', 4946686: 'Relative velocity', 27979: 'Sunlight', 11529: 'Fermion', 32498: 'Volume', 40170957: 'Action at a distance (physics)', 1201321: 'Superposition principle', 1126641: 'Invariant (physics)', 55442288: 'Constant factor rule in differentiation', 17327: 'Kinetic energy', 23912155: 'Gauge theory', 4838571: 'Position operator', 45151465: 'Natural phenomenon', 207833: 'Radial velocity', 291928: 'Operator (physics)', 198319: 'Hamiltonian mechanics', 173961: 'Center of mass', 20903754: 'Robotics'}
    word_id = [174593, 18947, 37892, 323592, 25098, 30731, 9228, 226829, 70671, 187408, 67088, 17939, 74263, 47641, 226841, 10779, 33306, 1111581, 1410595, 1912367, 312881, 59958, 29688374, 5176, 1979961, 49720, 27709, 14909, 48991805, 28736, 41026, 286788, 28748, 308815, 339024, 169552, 1949268, 74327, 230488, 455769, 291928, 45489756, 946273, 19555, 20580, 19593829, 276582, 19559, 19048, 33692268, 2053742, 25202, 154739, 852089, 1251967, 2217599, 12320384, 20491903, 25219, 19265670, 10890, 33931, 26764, 48781, 150159, 28305, 17553, 275603, 857235, 9505941, 10902, 256662, 22222481, 76954, 606874, 2010784, 531104, 27808, 1072289, 1201321, 4838571, 198319, 37750448, 4795569, 25267, 561845, 711862, 643769, 239290, 30214333, 30400, 5826, 28486339, 24577221, 266950, 31429, 18993869, 3071186, 1911511, 21210, 23912155, 1135324, 25312, 2274, 45151465, 426219, 8429, 19694, 719601, 32498, 1126641, 191734, 12024, 1025272, 36001020, 4946686, 2916607, 19555586, 30876419, 26884, 38293253, 11529, 5390, 295183, 26998547, 32533, 2839, 19737, 415513, 593693, 5918, 56683807, 49210145, 14627, 176931, 22308, 9723174, 82728, 6956, 54386990, 38318895, 265006, 5155121, 47922, 308, 174396, 9532, 3406142, 458558, 6513985, 17730, 13989702, 5961, 20903754, 27979, 1686861, 2434383, 494418, 26962, 474962, 26998617, 579929, 72540, 149861, 18589032, 33130, 157550, 297839, 36269934, 55442288, 2218355, 22393, 288123, 692093, 475008, 244611, 9604, 51079, 204680, 173961, 3691915, 2443, 11051917, 15221133, 61335, 10886039, 610202, 23536538, 60828, 22939, 19194778, 25453985, 2288549, 51624, 473514, 55212, 17327, 9649, 34753458, 172466, 25525, 11132342, 145343, 102338, 644550, 182727, 1968588, 40170957, 17384910, 20431, 211922, 15314901, 327127, 495065, 207833, 991, 1209823, 18404, 33445862, 34083818, 2413037, 17902, 7954422, 14838, 240123, 2434557, 12286, 189951]

    A = dict()
    # 12 25 12
    rwr = RWR()
    rwr.read_graph('path.txt', "undirected")
    r = rwr.compute(seed=seeds, max_iters=100)
    index = 0
    for i in r:
        index = index + 1
        if i > 1e-12:
            if (index + 11) in word_id:
                A[dic[index + 11]] = i[0]
    return A



A_RWR2 = dict()
def PRP():
    global A_RWR2
    dic = {2010784: 'Ivor Grattan-Guinness', 286788: 'Galilean invariance', 59958: 'Power series', 6513985: 'Mass in general relativity', 266950: "Galileo's ship", 275603: 'Frank Wilczek', 28305: 'String theory', 991: 'Absolute value', 295183: 'Galilean transformation', 33445862: 'Center of momentum frame', 61335: 'Differentiation', 644550: 'Higgs mechanism', 1410595: 'Field equation', 308: 'Aristotle', 18947: 'Metre', 14627: 'Isaac Newton', 26884: 'Superconductivity', 38318895: 'Bra-ket notation', 8429: 'Density', 857235: 'Equivalence principle', 244611: "Newton's law of universal gravitation", 25098: 'Phase velocity', 711862: 'Reissner–Nordström metric', 339024: 'Length contraction', 17553: "Kepler's laws of planetary motion", 288123: 'Worcester Polytechnic Institute', 531104: 'Gravity Probe B', 82728: 'Quantum superposition', 494418: 'Proper time', 12024: 'General relativity', 56683807: 'Shapiro delay', 10902: 'Force', 169552: 'Fifth force', 5961: 'Cognitive psychology', 30876419: 'Quantum state', 2053742: 'Contact force', 25525: 'René Descartes', 34753458: "Gauss' law for gravity", 610202: 'Fine structure', 60828: 'Lepton', 256662: 'Terminal velocity', 11051917: 'Günter Nimtz', 172466: 'Jean Buridan', 51079: 'Magnet', 9604: 'Many-worlds interpretation', 3406142: 'Relativity of simultaneity', 327127: 'John Archibald Wheeler', 76954: 'Parallel', 30400: 'Torque', 26764: 'International System of Units', 19555586: 'Classical mechanics', 74263: 'Frame of reference', 27808: 'Samuel Pepys', 9649: 'Energy', 149861: 'Work (physics)', 23536538: 'University of Wisconsin–Madison', 28736: 'Speed of light', 54386990: 'Absolute time and space', 5826: 'Complex number', 9723174: 'Irwin I. Shapiro', 7954422: 'Cornell University', 48781: 'Philosophiæ Naturalis Principia Mathematica', 495065: 'Louis Essen', 27709: 'Semiconductor', 25219: 'Quanta', 25202: 'Quantum mechanics', 22393: 'Organelle', 37892: 'Thrust', 5155121: 'Richard S. Westfall', 17902: 'Leonhard Euler', 2274: 'Arthur Eddington', 29688374: 'Galileo Galilei', 36001020: 'Gravitational wave detector', 1686861: 'Aether drag hypothesis', 12286: 'Great Plague of London', 5176: 'Calculus', 189951: 'Force carrier', 24577221: 'De Historia piscium', 191734: 'Godfrey Kneller', 55212: "Newton's laws of motion", 19593829: 'Spin (physics)', 174593: 'Socinianism', 1979961: "Torricelli's equation", 276582: 'Ricci curvature', 176931: 'Internet Archive', 6956: 'Conservation law', 174396: 'Bohr radius', 10779: 'Frequency', 561845: 'Jacob Bekenstein', 51624: 'Thomas Young (scientist)', 1135324: 'Conjugate variables', 606874: 'Einstein–Cartan theory', 719601: 'MIT Press', 187408: 'Self-adjoint operator', 154739: 'Electrodeposition', 312881: 'Action (physics)', 33931: 'Weight', 458558: 'List of scientific laws named after people', 426219: 'Classical electromagnetism', 22939: 'Physics', 26998617: 'Field (physics)', 25453985: 'Atomic clock', 2434383: 'Local reference frame', 4795569: 'Hypotheses non fingo', 28748: 'Speed', 37750448: 'Taub-NUT vacuum', 643769: 'Quantum tunnelling', 34083818: 'Entailment', 145343: 'Wave function', 9505941: 'List of quantum-mechanical systems with analytical solutions', 19694: 'Mercury (planet)', 21210: 'Niels Bohr', 239290: 'John Wallis', 10890: 'Fundamental interaction', 72540: 'Newton (unit)', 3691915: '0 (number)', 32533: 'Euclidean vector', 45489756: 'Gravitation', 1251967: 'Princeton University Press', 49210145: 'Coriolis effect', 47641: 'Standard Model', 579929: 'Rest (physics)', 15314901: 'Proper velocity', 33692268: 'List of topics named after Leonhard Euler', 26998547: 'Degrees of freedom (physics and chemistry)', 22222481: "Euler's laws of motion", 2217599: 'Circular symmetry', 1912367: 'Electromagnetic tensor', 204680: 'Four-momentum', 74327: 'Principle of relativity', 1072289: 'Uniform circular motion', 1949268: 'Aether (classical element)', 230488: 'Minkowski space', 30731: 'Teleological argument', 852089: 'Gravitational time dilation', 297839: 'Time dilation', 2288549: 'Momentum operator', 3071186: 'Gravitational acceleration', 26962: 'Special relativity', 22308: 'Oxford', 150159: "Noether's theorem", 19265670: 'Centrifugal force', 2218355: 'Astronomia nova', 2434557: 'Non-inertial reference frame', 14838: 'Inertial frame of reference', 475008: 'Stiffness', 1968588: 'Hypervelocity', 49720: 'Robert Hooke', 182727: "Mach's principle", 2916607: 'Force field (physics)', 265006: 'Dialogue Concerning the Two Chief World Systems', 473514: 'Generalized coordinates', 31429: 'Twin paradox', 9228: 'Earth', 226841: 'Four-acceleration', 2443: 'Acceleration', 12320384: 'Theory of impetus', 474962: 'Translation (physics)', 67088: 'Conservation of energy', 1111581: 'Reaction (physics)', 10886039: 'SunPower', 47922: 'Determinism', 25267: 'Quantum field theory', 20431: 'Momentum', 2413037: 'Isolated system', 240123: 'Plasticity (physics)', 308815: 'Connection (mathematics)', 19555: 'Molecule', 19048: 'Mass', 18404: 'Lorentz transformation', 2839: 'Angular momentum', 11132342: 'Bodmer Library', 33306: 'Water', 455769: 'Rigid body', 5390: 'Conversion of units', 157550: 'Karl Schwarzschild', 25312: 'Quantum gravity', 20491903: 'Velocity', 19737: "Maxwell's equations", 15221133: 'Euler force', 593693: 'Point (geometry)', 211922: 'Impulse (physics)', 18993869: 'Gas', 1209823: 'Rotating reference frame', 17939: 'Light', 692093: 'Oxford University Museum of Natural History', 30214333: '1st Earl of Halifax', 19194778: 'Deformation (mechanics)', 41026: 'Dielectric', 415513: 'Net force', 18589032: 'Particle accelerator', 36269934: 'Geodesic (general relativity)', 28486339: 'Georgia Institute of Technology', 13989702: 'Wolfram Demonstrations Project', 102338: 'Henry Cavendish', 1911511: 'Lorentz scalar', 1025272: 'Bohr–Einstein debates', 946273: 'Harry Ransom Center', 9532: 'Electromagnetism', 5918: 'Continuum mechanics', 20580: 'Motion (physics)', 14909: 'Inertia', 17384910: 'Observer (special relativity)', 70671: 'Stress–energy tensor', 17730: 'Latin', 48991805: 'Contributors to general relativity', 323592: 'Nicolaus Copernicus', 226829: 'Four-velocity', 33130: 'Werner Heisenberg', 38293253: 'Geodesic deviation equation', 19559: 'Mechanics', 4946686: 'Relative velocity', 27979: 'Sunlight', 11529: 'Fermion', 32498: 'Volume', 40170957: 'Action at a distance (physics)', 1201321: 'Superposition principle', 1126641: 'Invariant (physics)', 55442288: 'Constant factor rule in differentiation', 17327: 'Kinetic energy', 23912155: 'Gauge theory', 4838571: 'Position operator', 45151465: 'Natural phenomenon', 207833: 'Radial velocity', 291928: 'Operator (physics)', 198319: 'Hamiltonian mechanics', 173961: 'Center of mass', 20903754: 'Robotics'}
    word_id = [174593, 18947, 37892, 323592, 25098, 30731, 9228, 226829, 70671, 187408, 67088, 17939, 74263, 47641, 226841, 10779, 33306, 1111581, 1410595, 1912367, 312881, 59958, 29688374, 5176, 1979961, 49720, 27709, 14909, 48991805, 28736, 41026, 286788, 28748, 308815, 339024, 169552, 1949268, 74327, 230488, 455769, 291928, 45489756, 946273, 19555, 20580, 19593829, 276582, 19559, 19048, 33692268, 2053742, 25202, 154739, 852089, 1251967, 2217599, 12320384, 20491903, 25219, 19265670, 10890, 33931, 26764, 48781, 150159, 28305, 17553, 275603, 857235, 9505941, 10902, 256662, 22222481, 76954, 606874, 2010784, 531104, 27808, 1072289, 1201321, 4838571, 198319, 37750448, 4795569, 25267, 561845, 711862, 643769, 239290, 30214333, 30400, 5826, 28486339, 24577221, 266950, 31429, 18993869, 3071186, 1911511, 21210, 23912155, 1135324, 25312, 2274, 45151465, 426219, 8429, 19694, 719601, 32498, 1126641, 191734, 12024, 1025272, 36001020, 4946686, 2916607, 19555586, 30876419, 26884, 38293253, 11529, 5390, 295183, 26998547, 32533, 2839, 19737, 415513, 593693, 5918, 56683807, 49210145, 14627, 176931, 22308, 9723174, 82728, 6956, 54386990, 38318895, 265006, 5155121, 47922, 308, 174396, 9532, 3406142, 458558, 6513985, 17730, 13989702, 5961, 20903754, 27979, 1686861, 2434383, 494418, 26962, 474962, 26998617, 579929, 72540, 149861, 18589032, 33130, 157550, 297839, 36269934, 55442288, 2218355, 22393, 288123, 692093, 475008, 244611, 9604, 51079, 204680, 173961, 3691915, 2443, 11051917, 15221133, 61335, 10886039, 610202, 23536538, 60828, 22939, 19194778, 25453985, 2288549, 51624, 473514, 55212, 17327, 9649, 34753458, 172466, 25525, 11132342, 145343, 102338, 644550, 182727, 1968588, 40170957, 17384910, 20431, 211922, 15314901, 327127, 495065, 207833, 991, 1209823, 18404, 33445862, 34083818, 2413037, 17902, 7954422, 14838, 240123, 2434557, 12286, 189951]
    pagerank = PageRank()
    pagerank.read_graph('path.txt', "undirected")
    r = pagerank.compute()
    index = 0
    for i in r:
        index = index + 1
        if i > 1e-12:
            if (index + 11) in word_id:
                A_RWR2[dic[index + 11]] = i[0]




def rwr2(seeds):
    dic = {2010784: 'Ivor Grattan-Guinness', 286788: 'Galilean invariance', 59958: 'Power series', 6513985: 'Mass in general relativity', 266950: "Galileo's ship", 275603: 'Frank Wilczek', 28305: 'String theory', 991: 'Absolute value', 295183: 'Galilean transformation', 33445862: 'Center of momentum frame', 61335: 'Differentiation', 644550: 'Higgs mechanism', 1410595: 'Field equation', 308: 'Aristotle', 18947: 'Metre', 14627: 'Isaac Newton', 26884: 'Superconductivity', 38318895: 'Bra-ket notation', 8429: 'Density', 857235: 'Equivalence principle', 244611: "Newton's law of universal gravitation", 25098: 'Phase velocity', 711862: 'Reissner–Nordström metric', 339024: 'Length contraction', 17553: "Kepler's laws of planetary motion", 288123: 'Worcester Polytechnic Institute', 531104: 'Gravity Probe B', 82728: 'Quantum superposition', 494418: 'Proper time', 12024: 'General relativity', 56683807: 'Shapiro delay', 10902: 'Force', 169552: 'Fifth force', 5961: 'Cognitive psychology', 30876419: 'Quantum state', 2053742: 'Contact force', 25525: 'René Descartes', 34753458: "Gauss' law for gravity", 610202: 'Fine structure', 60828: 'Lepton', 256662: 'Terminal velocity', 11051917: 'Günter Nimtz', 172466: 'Jean Buridan', 51079: 'Magnet', 9604: 'Many-worlds interpretation', 3406142: 'Relativity of simultaneity', 327127: 'John Archibald Wheeler', 76954: 'Parallel', 30400: 'Torque', 26764: 'International System of Units', 19555586: 'Classical mechanics', 74263: 'Frame of reference', 27808: 'Samuel Pepys', 9649: 'Energy', 149861: 'Work (physics)', 23536538: 'University of Wisconsin–Madison', 28736: 'Speed of light', 54386990: 'Absolute time and space', 5826: 'Complex number', 9723174: 'Irwin I. Shapiro', 7954422: 'Cornell University', 48781: 'Philosophiæ Naturalis Principia Mathematica', 495065: 'Louis Essen', 27709: 'Semiconductor', 25219: 'Quanta', 25202: 'Quantum mechanics', 22393: 'Organelle', 37892: 'Thrust', 5155121: 'Richard S. Westfall', 17902: 'Leonhard Euler', 2274: 'Arthur Eddington', 29688374: 'Galileo Galilei', 36001020: 'Gravitational wave detector', 1686861: 'Aether drag hypothesis', 12286: 'Great Plague of London', 5176: 'Calculus', 189951: 'Force carrier', 24577221: 'De Historia piscium', 191734: 'Godfrey Kneller', 55212: "Newton's laws of motion", 19593829: 'Spin (physics)', 174593: 'Socinianism', 1979961: "Torricelli's equation", 276582: 'Ricci curvature', 176931: 'Internet Archive', 6956: 'Conservation law', 174396: 'Bohr radius', 10779: 'Frequency', 561845: 'Jacob Bekenstein', 51624: 'Thomas Young (scientist)', 1135324: 'Conjugate variables', 606874: 'Einstein–Cartan theory', 719601: 'MIT Press', 187408: 'Self-adjoint operator', 154739: 'Electrodeposition', 312881: 'Action (physics)', 33931: 'Weight', 458558: 'List of scientific laws named after people', 426219: 'Classical electromagnetism', 22939: 'Physics', 26998617: 'Field (physics)', 25453985: 'Atomic clock', 2434383: 'Local reference frame', 4795569: 'Hypotheses non fingo', 28748: 'Speed', 37750448: 'Taub-NUT vacuum', 643769: 'Quantum tunnelling', 34083818: 'Entailment', 145343: 'Wave function', 9505941: 'List of quantum-mechanical systems with analytical solutions', 19694: 'Mercury (planet)', 21210: 'Niels Bohr', 239290: 'John Wallis', 10890: 'Fundamental interaction', 72540: 'Newton (unit)', 3691915: '0 (number)', 32533: 'Euclidean vector', 45489756: 'Gravitation', 1251967: 'Princeton University Press', 49210145: 'Coriolis effect', 47641: 'Standard Model', 579929: 'Rest (physics)', 15314901: 'Proper velocity', 33692268: 'List of topics named after Leonhard Euler', 26998547: 'Degrees of freedom (physics and chemistry)', 22222481: "Euler's laws of motion", 2217599: 'Circular symmetry', 1912367: 'Electromagnetic tensor', 204680: 'Four-momentum', 74327: 'Principle of relativity', 1072289: 'Uniform circular motion', 1949268: 'Aether (classical element)', 230488: 'Minkowski space', 30731: 'Teleological argument', 852089: 'Gravitational time dilation', 297839: 'Time dilation', 2288549: 'Momentum operator', 3071186: 'Gravitational acceleration', 26962: 'Special relativity', 22308: 'Oxford', 150159: "Noether's theorem", 19265670: 'Centrifugal force', 2218355: 'Astronomia nova', 2434557: 'Non-inertial reference frame', 14838: 'Inertial frame of reference', 475008: 'Stiffness', 1968588: 'Hypervelocity', 49720: 'Robert Hooke', 182727: "Mach's principle", 2916607: 'Force field (physics)', 265006: 'Dialogue Concerning the Two Chief World Systems', 473514: 'Generalized coordinates', 31429: 'Twin paradox', 9228: 'Earth', 226841: 'Four-acceleration', 2443: 'Acceleration', 12320384: 'Theory of impetus', 474962: 'Translation (physics)', 67088: 'Conservation of energy', 1111581: 'Reaction (physics)', 10886039: 'SunPower', 47922: 'Determinism', 25267: 'Quantum field theory', 20431: 'Momentum', 2413037: 'Isolated system', 240123: 'Plasticity (physics)', 308815: 'Connection (mathematics)', 19555: 'Molecule', 19048: 'Mass', 18404: 'Lorentz transformation', 2839: 'Angular momentum', 11132342: 'Bodmer Library', 33306: 'Water', 455769: 'Rigid body', 5390: 'Conversion of units', 157550: 'Karl Schwarzschild', 25312: 'Quantum gravity', 20491903: 'Velocity', 19737: "Maxwell's equations", 15221133: 'Euler force', 593693: 'Point (geometry)', 211922: 'Impulse (physics)', 18993869: 'Gas', 1209823: 'Rotating reference frame', 17939: 'Light', 692093: 'Oxford University Museum of Natural History', 30214333: '1st Earl of Halifax', 19194778: 'Deformation (mechanics)', 41026: 'Dielectric', 415513: 'Net force', 18589032: 'Particle accelerator', 36269934: 'Geodesic (general relativity)', 28486339: 'Georgia Institute of Technology', 13989702: 'Wolfram Demonstrations Project', 102338: 'Henry Cavendish', 1911511: 'Lorentz scalar', 1025272: 'Bohr–Einstein debates', 946273: 'Harry Ransom Center', 9532: 'Electromagnetism', 5918: 'Continuum mechanics', 20580: 'Motion (physics)', 14909: 'Inertia', 17384910: 'Observer (special relativity)', 70671: 'Stress–energy tensor', 17730: 'Latin', 48991805: 'Contributors to general relativity', 323592: 'Nicolaus Copernicus', 226829: 'Four-velocity', 33130: 'Werner Heisenberg', 38293253: 'Geodesic deviation equation', 19559: 'Mechanics', 4946686: 'Relative velocity', 27979: 'Sunlight', 11529: 'Fermion', 32498: 'Volume', 40170957: 'Action at a distance (physics)', 1201321: 'Superposition principle', 1126641: 'Invariant (physics)', 55442288: 'Constant factor rule in differentiation', 17327: 'Kinetic energy', 23912155: 'Gauge theory', 4838571: 'Position operator', 45151465: 'Natural phenomenon', 207833: 'Radial velocity', 291928: 'Operator (physics)', 198319: 'Hamiltonian mechanics', 173961: 'Center of mass', 20903754: 'Robotics'}
    word_id = [174593, 18947, 37892, 323592, 25098, 30731, 9228, 226829, 70671, 187408, 67088, 17939, 74263, 47641, 226841, 10779, 33306, 1111581, 1410595, 1912367, 312881, 59958, 29688374, 5176, 1979961, 49720, 27709, 14909, 48991805, 28736, 41026, 286788, 28748, 308815, 339024, 169552, 1949268, 74327, 230488, 455769, 291928, 45489756, 946273, 19555, 20580, 19593829, 276582, 19559, 19048, 33692268, 2053742, 25202, 154739, 852089, 1251967, 2217599, 12320384, 20491903, 25219, 19265670, 10890, 33931, 26764, 48781, 150159, 28305, 17553, 275603, 857235, 9505941, 10902, 256662, 22222481, 76954, 606874, 2010784, 531104, 27808, 1072289, 1201321, 4838571, 198319, 37750448, 4795569, 25267, 561845, 711862, 643769, 239290, 30214333, 30400, 5826, 28486339, 24577221, 266950, 31429, 18993869, 3071186, 1911511, 21210, 23912155, 1135324, 25312, 2274, 45151465, 426219, 8429, 19694, 719601, 32498, 1126641, 191734, 12024, 1025272, 36001020, 4946686, 2916607, 19555586, 30876419, 26884, 38293253, 11529, 5390, 295183, 26998547, 32533, 2839, 19737, 415513, 593693, 5918, 56683807, 49210145, 14627, 176931, 22308, 9723174, 82728, 6956, 54386990, 38318895, 265006, 5155121, 47922, 308, 174396, 9532, 3406142, 458558, 6513985, 17730, 13989702, 5961, 20903754, 27979, 1686861, 2434383, 494418, 26962, 474962, 26998617, 579929, 72540, 149861, 18589032, 33130, 157550, 297839, 36269934, 55442288, 2218355, 22393, 288123, 692093, 475008, 244611, 9604, 51079, 204680, 173961, 3691915, 2443, 11051917, 15221133, 61335, 10886039, 610202, 23536538, 60828, 22939, 19194778, 25453985, 2288549, 51624, 473514, 55212, 17327, 9649, 34753458, 172466, 25525, 11132342, 145343, 102338, 644550, 182727, 1968588, 40170957, 17384910, 20431, 211922, 15314901, 327127, 495065, 207833, 991, 1209823, 18404, 33445862, 34083818, 2413037, 17902, 7954422, 14838, 240123, 2434557, 12286, 189951]
    A = dict()
    # 1 1 1
    rwr = RWR()
    rwr.read_graph('path2.txt', "undirected")
    r = rwr.compute(seed=seeds, max_iters=100)
    index = 0
    for i in r:
        index = index + 1
        if i > 1e-12:
            if (index + 0) in word_id:
                A[dic[index + 0]] = i[0]
    return A





# 词条d的第一个句子是否包含了d1的title
def f1(A, B):
    summ = summary(A)
    A_sum = summ[0:summ.find(".")]
    if B.lower() in A_sum.lower():
        return 1
    else:
        return 0
    pass


# 词条d的概述部分是否引用了词条d1
def f2(A, B):
    first_title = summary(A)
    return 1 if first_title.find(B) != -1 else 0
    pass


# 词条d与词条d1的分类，是否有重叠
def f3(A, B):
    A_categorys = categories(A)
    B_categorys = categories(B)
    return 1 if len(A_categorys & B_categorys) > 0 else 0
    pass


# 词条d引用了词条d1的次数的log值
import math
def f4(A, B):
    num = tf(B, A)
    if num == 0:
        num = 1
    return math.log10(num)
    pass






if __name__ == '__main__':
    PRP()
    keys = ['Classical mechanics', 'Energy', "Euler's laws of motion", 'Force', 'Frame of reference', 'Galilean invariance', 'General relativity', 'Gravitation', 'Inertial frame of reference', 'Isaac Newton', 'Mass', 'Momentum', 'Net force', "Newton's laws of motion", 'Philosophiæ Naturalis Principia Mathematica', 'Quantum mechanics', 'Special relativity', 'Speed of light', 'Velocity']
    words = [['Quanta', 'Degrees of freedom (physics and chemistry)', 'Quantum tunnelling', 'Gas', 'Complex number', '0 (number)', 'Kinetic energy', 'Acceleration', 'Cornell University', 'Action (physics)'],
             ['Light', 'Water', 'Lorentz scalar', 'SunPower', 'Thomas Young (scientist)', 'Conjugate variables', 'Organelle'],
             ['List of topics named after Leonhard Euler', "Newton's laws of motion", 'Center of mass', 'Rigid body', 'Torque', 'Momentum'],
             ['Newton (unit)', 'Magnet', 'Torque', 'Circular symmetry', 'Gravitational acceleration', 'Fermion', 'Volume', 'Lepton', 'Electromagnetism', 'Henry Cavendish', 'Theory of impetus', 'Coriolis effect'],
             ['Cognitive psychology', 'Standard Model', 'Generalized coordinates', 'Quantum field theory', "Mach's principle", 'Special relativity', 'Karl Schwarzschild', 'Quantum gravity', 'Coriolis effect', 'Robotics', 'Atomic clock'],
             ['Center of momentum frame', 'Momentum', 'Lorentz transformation', "Maxwell's equations", 'Inertial frame of reference', 'Work (physics)', 'Absolute time and space', "Galileo's ship", 'Isolated system', 'Electromagnetism', 'Dialogue Concerning the Two Chief World Systems'],
             ['Conservation of energy', 'Reissner–Nordström metric', 'Acceleration', 'Gravity Probe B', 'Taub-NUT vacuum', 'Field equation', 'Earth', 'Stress–energy tensor', 'Shapiro delay', 'Geodesic (general relativity)', 'Contributors to general relativity', 'Density', 'Atomic clock', 'Einstein–Cartan theory', 'Connection (mathematics)', 'Equivalence principle', 'Proper time', "Newton's laws of motion", 'Inertial frame of reference', 'Gravitational wave detector', 'Ricci curvature'],
             ['Natural phenomenon', 'Force', 'Irwin I. Shapiro', 'Arthur Eddington', 'Aristotle', 'Fifth force', 'Jacob Bekenstein', 'Fundamental interaction', 'Contact force', "Gauss' law for gravity"],
             ['Absolute time and space', 'Force', 'Rest (physics)', 'Length contraction', 'Velocity', 'Rotating reference frame', 'Acceleration', 'Coriolis effect', 'Force carrier', 'Galilean transformation', 'Euler force', 'Metre', 'Speed of light', 'Conversion of units', 'Centrifugal force', 'Euclidean vector', 'Translation (physics)', 'Non-inertial reference frame', 'Geodesic deviation equation', 'John Archibald Wheeler', 'Entailment', 'Local reference frame'],
             ['Great Plague of London', 'Power series', '1st Earl of Halifax', 'Earth', 'Internet Archive', 'René Descartes', 'Socinianism', 'Oxford University Museum of Natural History', 'MIT Press', 'Godfrey Kneller'],
             ['Classical mechanics', 'Weight', 'Physics', 'Electrodeposition', 'Mass in general relativity', 'Mercury (planet)', 'Bohr radius', 'Frank Wilczek', 'Gravitational time dilation'],
             ['Four-momentum', 'Frame of reference', 'Werner Heisenberg', 'International System of Units', 'Dielectric', 'Jean Buridan', 'Proper time', 'Conservation law', 'Quantum mechanics', 'Stiffness', 'John Wallis'],
             ['Point (geometry)', 'Euclidean vector', 'Absolute value', 'Mechanics'],
             ['List of scientific laws named after people', 'Wave function', 'Semiconductor', 'Parallel', 'Differentiation', 'Philosophiæ Naturalis Principia Mathematica', 'Hypotheses non fingo', 'Continuum mechanics', 'Principle of relativity', 'Acceleration', 'Angular momentum', 'Thrust', 'Inertia', 'Motion (physics)', "Newton's law of universal gravitation", 'Action at a distance (physics)', 'Fermion', 'Spin (physics)', 'Center of mass', "Kepler's laws of planetary motion", 'Force', "Noether's theorem", 'Standard Model', 'Superconductivity', 'Net force', 'Conservation law', 'Deformation (mechanics)', 'Impulse (physics)', 'René Descartes', 'Constant factor rule in differentiation', 'Energy', 'Force field (physics)', 'Gauge theory', 'Momentum operator', 'Isaac Newton', 'Operator (physics)', 'Classical mechanics', 'Momentum', 'Reaction (physics)', 'Quantum state', 'Special relativity', 'Leonhard Euler', 'Uniform circular motion', 'Mass', 'Classical electromagnetism', 'Plasticity (physics)', 'Hamiltonian mechanics', 'Speed of light', 'Wolfram Demonstrations Project'],
             ['Calculus', 'Astronomia nova', 'University of Wisconsin–Madison', 'Richard S. Westfall', 'Aether (classical element)', 'Nicolaus Copernicus', 'Oxford', 'Teleological argument', 'Latin', 'Samuel Pepys', 'De Historia piscium', 'Robert Hooke', 'Worcester Polytechnic Institute', 'Ivor Grattan-Guinness', "Kepler's laws of planetary motion", 'Bodmer Library', 'Harry Ransom Center', 'Georgia Institute of Technology'],
             ['Niels Bohr', 'Bra-ket notation', 'Self-adjoint operator', 'List of quantum-mechanical systems with analytical solutions', 'Princeton University Press', 'Position operator', 'Frequency', 'Molecule', 'Superposition principle', 'Many-worlds interpretation', 'Determinism', 'Bohr–Einstein debates', 'Quantum superposition'],
             ['Günter Nimtz', 'String theory', 'Galileo Galilei', 'Gravitation', 'Velocity', 'Absolute time and space', 'Time dilation', 'Mass', 'Frame of reference', 'Invariant (physics)', 'Fine structure', 'Four-acceleration', 'Particle accelerator', 'Relativity of simultaneity', 'Electromagnetic tensor', 'Observer (special relativity)', 'Twin paradox', 'Aether drag hypothesis'],
             ['Field (physics)', 'Sunlight', 'Isaac Newton', 'International System of Units', 'Louis Essen', 'Higgs mechanism'],
             ['Hypervelocity', 'Classical mechanics', 'Four-velocity', 'Radial velocity', 'Terminal velocity', 'Phase velocity', "Torricelli's equation", 'Relative velocity', 'Minkowski space', 'Speed', 'Proper velocity']]
    f = open("text.txt","w",encoding='gb2312')
    for d in range(len(keys)):
        start = timeit.default_timer()
        A = keys[d]
        A_id = pageid(A)
        A_RWR = rwr(A_id)
        A_RWR3 = rwr2(A_id)
        print(str(d / len(keys)))
        timeuse("初始化")
        for d1 in range(len(words[d])):
            B = words[d][d1]
            try:
                RWR_1 = A_RWR[B]
            except KeyError as ke:
                print(ke)
                RWR_1 = 0

            try:
                RWR_2 = A_RWR2[B] - A_RWR2[A]
            except KeyError as ke:
                print(ke)
                RWR_2 = 0 - A_RWR2[A]

            try:
                RWR_3 = A_RWR3[B]
            except KeyError as ke:
                print(ke)
                RWR_3 = 0

            f_1 = f1(A, B)
            f_2 = f2(A, B)
            f_3 = f3(A, B)
            f_4 = f4(A, B)
            print(A + ',' + B + ',' + str(RWR_1) + ',' + str(RWR_2) + ',' + str(RWR_3) + ',' +
                  str(f_1) + ',' + str(f_2) + ',' + str(f_3) + ',' + str(f_4))
            try:
                f.write(A + ',' + B + ',' + str(RWR_1) + ',' + str(RWR_2) + ',' + str(RWR_3) + ',' +
                      str(f_1) + ',' + str(f_2) + ',' + str(f_3) + ',' + str(f_4) + "\n")
            except UnicodeEncodeError as un:
                print(un)
                f.write('--------------------------' + "\n")
            else:
                pass
            pass
    f.close()

















































