# -*- coding:utf-8 -*-
# Author:Zhou Yang



#         (sita, 1]        if B is a prerequisite of A   (B -> A  2)
#
#  redf = [-sita, sita]    if no prerequisite relation   (null    3(3, 4, 5))
#
#         [-1, -sita)      if A is a prerequisite of B   (A -> B  1)

# refd = (value1 / value2 - value3 / value4), value1, value2, value3, value4





import numpy
import xlrd
import decimal
import matplotlib.pyplot as plt
from pylab import *
mpl.rcParams['font.sans-serif'] = ['SimHei']





if __name__ == '__main__':

    begin = 1346
    end = 1546
    datas, result = list(), list()

    maxtri = [[0,0,0],[0,0,0],[0,0,0]]
    data = xlrd.open_workbook(r'C:\Users\Desktop\ddd.xls')
    table = data.sheets()[0]
    for i in range(begin, end):
        datas.append((decimal.Decimal(table.row_values(i)[0]),int(table.row_values(i)[1])))

    for i in datas:
        print(i)

    right, cla = 0, 0

    sita = 0.5421
    for obj in datas:

        pre = -1
        if -1.0 <= round(obj[0], 1) and obj[0] < -1 * sita:
            pre = 1
        elif -1 * sita <= obj[0] and obj[0] <=sita:
            pre = 3
        elif sita < obj[0] and round(obj[0], 1) <= 1.0:
            pre = 2
        else:
            pass
        if pre == -1:
            continue

        cla = cla + 1
        if pre == obj[1]:
            right = right + 1
        maxtri[obj[1]-1][pre-1] = maxtri[obj[1]-1][pre-1] + 1
    print(cla)
    print("sita为" + str(sita) + "总分类结果的正确率")
    print('%.2f%%' % (right / cla * 100))
    print(right)



    for i in range(3):
        for j in range(3):
            print(maxtri[i][j],end=" ")
        print()







































































































