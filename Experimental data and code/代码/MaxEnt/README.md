# Maxent
java实现，此最大熵结合了java上opennlp包的最大熵及python上nltk的最大熵部分实现，并进行了一些改动。算法包含gis及iis实现，内含详细中文注释，附上训练及测试数据<br>

# 训练执行截图:<br>
![image](https://github.com/sccuncai/Maxent/raw/master/Screenshots/1.png)

# 执行函数截图：<br>
![image](https://github.com/sccuncai/Maxent/raw/master/Screenshots/2.png)

在MaxentTrainer 类中进行主要参数设置<br>
![image](https://github.com/sccuncai/Maxent/raw/master/Screenshots/3.png)<br>
1.isDiscretization=false，输入特征数据为类别类型<br>
2.isDiscretization=true，输入特征数据为数值类型，同时需要设置 section 的值，值越大越准确，内存消耗越多，根据实际效果调整<br>

# 适用范围及数据格式
训练数据：<br>
1.类别类型数据举例<br>
"featureName1=a featureName2=b 1"<br>

2.数值类型数据举例<br>
"featureName1=10 featureName2=11 featureName3=12 1"<br>

=号左边为特征名称，右边为值，最后一列为标记结果<br>

3.测试数据：<br>
如提供的 test_data.txt，最后一列标记结果是用来验证预测结果和标记结果用的。<br>
测试数据可以为，例如："featureName1=10 featureName2=11 featureName3=12",省略最后的标记，作为线上预测<br>


# 注意事项<br>
1.如果输入数据同时包含类别类型和数值类型，那么麻烦去数值处理的类 ContinuumToDiscretization 中自行处理，或者新增自定义的类。<br>
2.如果实际迭代次数小于设置的迭代次数，去类 ConvergenceCheck 中调整下参数（使用iis的话 MaxentClassifierWithIis 类中可调整牛顿切线法的参数）<br>
![image](https://github.com/sccuncai/Maxent/raw/master/Screenshots/4.png)

有问题可联系我qq：30262400

# 附加信息
有兴趣请关注我的其他项目<br>
https://github.com/sccuncai/SComicReader.git
