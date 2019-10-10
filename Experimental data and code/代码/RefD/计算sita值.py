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

matplotlib.rcParams['axes.unicode_minus']=False



if __name__ == '__main__':

    step = 0.0001
    row = 400
    rd = len(str(step)[int(str(step).find(".")): -1])
    datas, result = list(), list()


    data = xlrd.open_workbook(r'C:\Users\Desktop\table1.xls')
    table = data.sheets()[0]
    for i in range(row):

        datas.append((decimal.Decimal(table.row_values(i)[0]),int(table.row_values(i)[1])))


    for sita in numpy.arange(0, 1 + step, step):
        right, cla = 0, 0

        # print(sita)
        for obj in datas:

            pre = -1
            # if -1.0 <= round(obj[0], 1) and obj[0] < -1 * sita:
            #     pre = 1
            # elif -1 * sita <= obj[0] and obj[0] <=sita:
            #     pre = 3
            # elif sita <= obj[0] and round(obj[0], 1) <= 1.0:
            #     pre = 2
            # else:
            #     pass

            # if -1.0 <= round(obj[0], 1) and obj[0] < 1 * sita:
            #     pre = 2
            # elif sita <= obj[0] and round(obj[0], 1) <= 1.0:
            #     pre = 1
            # else:
            #     pass



            if obj[0] < 1 * sita:
                pre = 2
            elif obj[0] >= 1 * sita:
                pre = 1
            else:
                pass





            cla += 1
            if pre == -1:
                cla -= 1
                continue

            if pre == obj[1]:
                right += 1

        result.append((right / cla, sita))
        print((right / cla, sita))

    max, index = -1, -1
    for index in range(len(result)):


        if result[index][0] > max:
            max = result[index][0]
            index = index

    print()
    print("max:", end="")
    print(max)
    print("sita:", result[index][1])



































































































