# -*- coding:utf-8 -*-
# Author:Zhou Yang


obj = [[4.6511627906976755e-05,4.6511627906976755e-05,0.09632558139534886,4.6511627906976755e-05,0.014000000000000004],
[0.0008771929824561404,0.3342105263157895,0.04473684210526316,0.0008771929824561404,0.0008771929824561404],
[0.0006993006993006993,0.0006993006993006993,0.0006993006993006993,0.0006993006993006993,0.0006993006993006993],
[0.0017684887459807072,0.00016077170418006429,0.18022508038585205,0.00016077170418006429,0.00016077170418006429],
[0.0006578947368421055,0.0006578947368421055,0.05328947368421053,0.02697368421052632,0.6256578947368422],
[0.02,0.1836363636363636,0.05636363636363636,0.4927272727272727,0.001818181818181818],
[0.014987714987714982,0.00024570024570024564,0.00024570024570024564,0.00024570024570024564,0.00024570024570024564],
[0.00035971223021582735,0.018345323741007193,0.00035971223021582735,0.14784172661870504,0.011151079136690648],
[0.00015082956259426846,0.04389140271493212,0.016742081447963797,0.015233785822021112,0.00015082956259426846],
[0.0003048780487804878,0.610060975609756,0.0003048780487804878,0.0003048780487804878,0.0003048780487804878],
[0.0009803921568627455,0.0009803921568627455,0.030392156862745108,0.0009803921568627455,0.030392156862745108],
[0.002439024390243902,0.002439024390243902,0.002439024390243902,0.002439024390243902,0.46585365853658534],
[0.007209302325581393,0.044418604651162784,0.04906976744186046,0.0025581395348837203,0.00023255813953488368],
[0.00012738853503184712,0.04980891719745222,0.00012738853503184712,0.5797452229299362,0.0026751592356687895],
[0.0018518518518518517,0.687037037037037,0.11296296296296295,0.03888888888888888,0.0018518518518518517],
[0.5854942233632862,0.042490372272143764,0.0014120667522464696,0.00012836970474967906,0.00526315789473684],
[0.0012500000000000002,0.16375000000000003,0.0012500000000000002,0.0012500000000000002,0.10125],
[0.0009900990099009901,0.0009900990099009901,0.0009900990099009901,0.0009900990099009901,0.0009900990099009901],
[0.0009900990099009901,0.1891089108910891,0.0009900990099009901,0.04059405940594059,0.0009900990099009901],
[0.00037313432835820896,0.011567164179104477,0.16082089552238807,0.015298507462686566,0.00037313432835820896],
[0.00026881720430107527,0.00026881720430107527,0.0029569892473118283,0.0029569892473118283,0.0029569892473118283],
[0.029710144927536236,0.5804347826086957,0.0007246376811594205,0.0007246376811594205,0.0007246376811594205],
[0.0019366197183098594,0.00017605633802816902,0.00545774647887324,0.00017605633802816902,0.07235915492957747],
[0.0002724795640326975,0.0002724795640326975,0.10653950953678472,0.0002724795640326975,0.0002724795640326975],
[0.0002695417789757412,0.0002695417789757412,0.0002695417789757412,0.0002695417789757412,0.0002695417789757412],
[0.0005555555555555556,0.0005555555555555556,0.0005555555555555556,0.0005555555555555556,0.0005555555555555556],
[0.002663438256658596,0.00024213075060532688,0.03171912832929782,0.00024213075060532688,0.03414043583535109],
[0.003853211009174311,0.00018348623853211006,0.00018348623853211006,0.00018348623853211006,0.00018348623853211006],
[7.90513833992095e-05,7.90513833992095e-05,0.010355731225296445,7.90513833992095e-05,7.90513833992095e-05]]



obj_sum = list()

import math

for i in obj:
    i_sum = 0
    for j in i:
        i_sum = i_sum + j * math.log10(j)
    obj_sum.append(-1 * i_sum)


print(len(obj_sum))

print(obj_sum)


for i in range(len(obj_sum) - 1):
    for j in range(i + 1, len(obj_sum)):
        print(str(obj_sum[i]) + "," + str(obj_sum[j]))


















































































