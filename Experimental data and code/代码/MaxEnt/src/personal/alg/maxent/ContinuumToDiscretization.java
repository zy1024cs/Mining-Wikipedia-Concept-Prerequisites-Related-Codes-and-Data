package personal.alg.maxent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class ContinuumToDiscretization {
	//每行保存训练数据中每列特征的离散化的分割点
	private Double[][] discretizationMatrix;

	//特征名称索引
	private Map<String,Integer> featureNameIndex;
	//划分多少个离散空间
	private int n;
	
	public ContinuumToDiscretization()
	{
		
	}
	
	/**
	 * @param n 划分区间数量
	 */
	public ContinuumToDiscretization(int n)
	{
		this.n = n;
	}
	/**
	 * 生成特征名称索引
	 * @param trainData
	 * @return
	 */
	private Map<String,Integer> getFeatureNameIndex(List<FeatureData> trainData)
	{
		//特征名称集合
		Set<String> featureNames = new HashSet<String>();
		//特征名称索引
		Map<String,Integer> featureNameIndex = new HashMap<String,Integer>();
		//保存特征名称
		for (FeatureData data : trainData) {
			featureNames.addAll(Arrays.asList(data.getNames()));
		}
		//保存特征名称索引
		int i = 0;
		for (String featureName : featureNames) {
//			System.out.println(featureName+" "+i);
			featureNameIndex.put(featureName, i++);
		}
		return featureNameIndex;
	}
	
	/**
	 * 生成特征值矩阵
	 * @param trainData
	 * @return
	 */
	private Double[][] getValueMatrix(List<FeatureData> trainData,Map<String,Integer> featureNameIndex)
	{
		
		//数组大小,初始值为null
		Double[][] valueMatrix = new Double[ trainData.size()][featureNameIndex.size()];
		try {
			for (int m = 0; m < trainData.size(); m++) {
				FeatureData data = trainData.get(m);
				//当前行的特征名称
				String[] names = data.getNames();
				//当前行的特征值
				String[] values = data.getValues();
				for (int n = 0; n < names.length; n++) {
					//当前特征名称的索引
					int index = featureNameIndex.get(names[n]);
					//数组赋值
					valueMatrix[m][index] = Double.parseDouble(values[n]);
				}
			}
		} 
		catch (ClassCastException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return valueMatrix;
	}
	
	/**
	 * 读取并处理数据,用于当前类main函数测试
	 * @param fileName
	 * @return
	 */
	private static List<FeatureData> getTrainData(String fileName)
	{
		List<FeatureData> trainData = null;
	    FileReader datafr = null;
		try {
			datafr = new FileReader(new File(fileName));
			FeatureDataStream es = new FeatureDataStream(new PlainTextByLineDataStreamS(datafr));
			trainData = Util.getTrainData(es);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return trainData;
	}
	
	/**
	 * 数组值离散化
	 * @param valuleArray
	 * @param n
	 */
	private Double[] valuleDiscretization(Double[] valuleArray,int n)
	{
		//如果当前行没有某个特征，则value矩阵中特征值为null,把它去掉
		List<Double> valuleList = new ArrayList<Double>();
		for (int i = 0; i < valuleArray.length; i++) {
			if(null != valuleArray[i])
			{
				valuleList.add(valuleArray[i]);
			}
		}
		Collections.sort(valuleList);
//		System.out.println(valuleList.toString());
		//去掉null值的结果
		Double[] valuleArray2 = (Double[])valuleList.toArray(new Double[valuleList.size()]);
		//从小到大排序,影响效率的主要因素
//		selectSort(valuleArray2);
//		System.out.println("selectSort");
		//区间值
		double sectionValue = (double)valuleArray2.length / n;
		//保存分割区间的值
		Double[] discretizationArray = null;
		if(sectionValue > 1.0)
		{
			double addSectionValue = sectionValue;
			discretizationArray = new Double[n];
			//选取分割点的值保存到discretizationArray数组
			int i = 0;
			
			for (int j = 0 ; j < valuleArray2.length; j ++) {
				if(i < n - 1  && j + 1 >= Math.floor(addSectionValue))
				{
					discretizationArray[i++] = valuleArray2[j];
//					System.out.println(valuleArray2[j]);
					addSectionValue += sectionValue;
					
				}
			}
			//由于可能存在精度问题，最后一个划分的数不为最小值。例如一共13个数，划分3个区间，
			//第一个位置4.3，第二个为8.6，第三个为12.9，取到的区间为位置为4，8，12，但最后一个实际为13。
			discretizationArray[i] = valuleArray2[valuleArray2.length - 1];
		}
		//小于等于1则当前每个值为1个区间
		else
		{
			discretizationArray = valuleArray2;
		}
		
//		System.out.println(Arrays.toString(discretizationArray));
		
		//去掉分割点的重复值
		List<Double> discretizationArray2 = new ArrayList<Double>();
		for (int j = 0; j < discretizationArray.length; j++) {
			if(!discretizationArray2.contains(discretizationArray[j]))
			{
				discretizationArray2.add(discretizationArray[j]);
			}
		}
		
//		System.out.println("------"+discretizationArray2.toString());
//		System.out.println("------"+Arrays.toString(valuleArray2));
		return (Double[])discretizationArray2.toArray(new Double[discretizationArray2.size()]);
//		return discretizationArray;
	}
	
	/**
	 * 把valueMatrix矩阵里每一列离散化，返回discretizationMatrix每行的长度不同，不是matrix，但取名字好听些
	 * @param valueMatrix
	 * @param n
	 */
	private Double[][] matrixDiscretization(Double[][] valueMatrix,int n)
	{
		int rowLen = valueMatrix.length;
		int colLen = valueMatrix[0].length;
		
		//每行保存训练数据中每列特征的离散化的分割点
		Double[][] discretizationMatrix = new Double[colLen][];
		//i为特征名称的索引
		for (int i = 0; i < colLen; i++) {
			Double[] valuleArray = new Double[rowLen];
 			for (int j = 0; j < rowLen; j++) {
 				valuleArray[j] = valueMatrix[j][i];
			}
 			
 			discretizationMatrix[i] = valuleDiscretization(valuleArray,n);
		}
		return discretizationMatrix;
	}
	
	/**
	 * 将每行数据离散化
	 * @param data
	 * @param discretizationMatrix
	 */
	public void dataDiscretization(FeatureData data,Double[][] discretizationMatrix,
			Map<String,Integer> featureNameIndex) {
			//当前行的特征名称
			String[] names = data.getNames();
			//当前行的特征值
			String[] values = data.getValues();

			for (int i = 0; i < values.length; i++) {
				Integer index = featureNameIndex.get(names[i]);
				//特征名称存在
				if(index != null)
				{

					Double newValue = valueDiscretization(discretizationMatrix[index],
							Double.parseDouble(values[i]));
					//重新赋离散化后的值
					values[i] = String.valueOf(newValue);
					
				}
			}
	}
	
	/**
	 * 每个值进行离散化
	 * @param discretizationArray 为从小到大排序的数组
	 * @param values
	 */
	private Double valueDiscretization(Double[] discretizationArray, Double values) {
		
		for (int i = 0; i < discretizationArray.length; i++) {
			if(values <= discretizationArray[i])
			{
				return discretizationArray[i];
			}
		}
		return discretizationArray[discretizationArray.length - 1];
	}
	
	/**
	 * 选择排序
	 * @param array
	 */
	public static void selectSort(Double[] array) {   
	    int size = array.length;
	    Double temp = 0.0;   
	    for (int i = 0; i < size; i++) {   
	        int k = i;   
	        for (int j = size - 1; j >i; j--)  {   
	            if (array[j] < array[k])  k = j;   
	        }   
	        temp = array[i];   
	        array[i] = array[k];   
	        array[k] = temp;   
	    }   
	}  
	
	/**
	 * 快速排序,数组大则出现StackOverflowError出错
	 * @param array 输入数组
	 */
	private void quickSort(Double[] array) {  
        quicksort(array, 0, array.length - 1); 
    }  
	
	private void quicksort(Double array[], int left, int right) {
        int dp;
        if (left < right) {
            dp = partition(array, left, right);
            quicksort(array, left, dp - 1);
            quicksort(array, dp + 1, right);
        }
    }
 
	private int partition(Double array[], int left, int right) {
		Double pivot = array[left];
        while (left < right) {
            while (left < right && array[right] >= pivot)
                right--;
            if (left < right)
                array[left++] = array[right];
            while (left < right && array[left] <= pivot)
                left++;
            if (left < right)
                array[right--] = array[left];
        }
        array[left] = pivot;
        return left;
    }
	
	/**
	 * 特征训练数据离散化
	 */
	public void disperse(List<FeatureData> trainData)
	{
		this.featureNameIndex = getFeatureNameIndex(trainData);
		Double[][] valueMatrix = getValueMatrix(trainData,this.featureNameIndex);
//		//输出矩阵
//		for (int i = 0; i < valueMatrix.length; i++) {
//			System.out.println(Arrays.toString(valueMatrix[i]));
//		}
		//离散化值的矩阵
		this.discretizationMatrix = matrixDiscretization(valueMatrix,this.n);
		//释放资源
		valueMatrix = null;
//		System.out.println("discretizationMatrix.length "+discretizationMatrix.length);
//		for (int i = 0; i < discretizationMatrix.length; i++) {
//			System.out.println(Arrays.toString(discretizationMatrix[i]));
//		}
		//将数据离散化
		for (FeatureData data : trainData) {
			dataDiscretization(data,this.discretizationMatrix,this.featureNameIndex);
		}
		
	}
	
	
	public Double[][] getDiscretizationMatrix() {
		return discretizationMatrix;
	}
	public void setDiscretizationMatrix(Double[][] discretizationMatrix) {
		this.discretizationMatrix = discretizationMatrix;
	}
	public Map<String, Integer> getFeatureNameIndex() {
		return featureNameIndex;
	}
	public void setFeatureNameIndex(Map<String, Integer> featureNameIndex) {
		this.featureNameIndex = featureNameIndex;
	}
	
	public static void main(String[] args) {
		String fileName = "realTeam.dat";
		List<FeatureData> trainData = getTrainData(fileName);
		ContinuumToDiscretization ctd = new ContinuumToDiscretization(4);
		ctd.disperse(trainData);
		System.out.println(trainData.size());
		for (FeatureData data : trainData) {
			//当前行的特征名称
			String[] names = data.getNames();
			//当前行的特征值
			String[] values = data.getValues();

			for (int i = 0; i < values.length; i++) {
				System.out.print(names[i]+" "+values[i]+" ");
			}
			System.out.println("----------");
		}
	}

	
}