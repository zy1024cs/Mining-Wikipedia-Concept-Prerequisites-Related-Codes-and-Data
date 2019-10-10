package personal.alg.maxent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class ContinuumToDiscretization {
	//ÿ�б���ѵ��������ÿ����������ɢ���ķָ��
	private Double[][] discretizationMatrix;

	//������������
	private Map<String,Integer> featureNameIndex;
	//���ֶ��ٸ���ɢ�ռ�
	private int n;
	
	public ContinuumToDiscretization()
	{
		
	}
	
	/**
	 * @param n ������������
	 */
	public ContinuumToDiscretization(int n)
	{
		this.n = n;
	}
	/**
	 * ����������������
	 * @param trainData
	 * @return
	 */
	private Map<String,Integer> getFeatureNameIndex(List<FeatureData> trainData)
	{
		//�������Ƽ���
		Set<String> featureNames = new HashSet<String>();
		//������������
		Map<String,Integer> featureNameIndex = new HashMap<String,Integer>();
		//������������
		for (FeatureData data : trainData) {
			featureNames.addAll(Arrays.asList(data.getNames()));
		}
		//����������������
		int i = 0;
		for (String featureName : featureNames) {
//			System.out.println(featureName+" "+i);
			featureNameIndex.put(featureName, i++);
		}
		return featureNameIndex;
	}
	
	/**
	 * ��������ֵ����
	 * @param trainData
	 * @return
	 */
	private Double[][] getValueMatrix(List<FeatureData> trainData,Map<String,Integer> featureNameIndex)
	{
		
		//�����С,��ʼֵΪnull
		Double[][] valueMatrix = new Double[ trainData.size()][featureNameIndex.size()];
		try {
			for (int m = 0; m < trainData.size(); m++) {
				FeatureData data = trainData.get(m);
				//��ǰ�е���������
				String[] names = data.getNames();
				//��ǰ�е�����ֵ
				String[] values = data.getValues();
				for (int n = 0; n < names.length; n++) {
					//��ǰ�������Ƶ�����
					int index = featureNameIndex.get(names[n]);
					//���鸳ֵ
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
	 * ��ȡ����������,���ڵ�ǰ��main��������
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
	 * ����ֵ��ɢ��
	 * @param valuleArray
	 * @param n
	 */
	private Double[] valuleDiscretization(Double[] valuleArray,int n)
	{
		//�����ǰ��û��ĳ����������value����������ֵΪnull,����ȥ��
		List<Double> valuleList = new ArrayList<Double>();
		for (int i = 0; i < valuleArray.length; i++) {
			if(null != valuleArray[i])
			{
				valuleList.add(valuleArray[i]);
			}
		}
		Collections.sort(valuleList);
//		System.out.println(valuleList.toString());
		//ȥ��nullֵ�Ľ��
		Double[] valuleArray2 = (Double[])valuleList.toArray(new Double[valuleList.size()]);
		//��С��������,Ӱ��Ч�ʵ���Ҫ����
//		selectSort(valuleArray2);
//		System.out.println("selectSort");
		//����ֵ
		double sectionValue = (double)valuleArray2.length / n;
		//����ָ������ֵ
		Double[] discretizationArray = null;
		if(sectionValue > 1.0)
		{
			double addSectionValue = sectionValue;
			discretizationArray = new Double[n];
			//ѡȡ�ָ���ֵ���浽discretizationArray����
			int i = 0;
			
			for (int j = 0 ; j < valuleArray2.length; j ++) {
				if(i < n - 1  && j + 1 >= Math.floor(addSectionValue))
				{
					discretizationArray[i++] = valuleArray2[j];
//					System.out.println(valuleArray2[j]);
					addSectionValue += sectionValue;
					
				}
			}
			//���ڿ��ܴ��ھ������⣬���һ�����ֵ�����Ϊ��Сֵ������һ��13����������3�����䣬
			//��һ��λ��4.3���ڶ���Ϊ8.6��������Ϊ12.9��ȡ��������Ϊλ��Ϊ4��8��12�������һ��ʵ��Ϊ13��
			discretizationArray[i] = valuleArray2[valuleArray2.length - 1];
		}
		//С�ڵ���1��ǰÿ��ֵΪ1������
		else
		{
			discretizationArray = valuleArray2;
		}
		
//		System.out.println(Arrays.toString(discretizationArray));
		
		//ȥ���ָ����ظ�ֵ
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
	 * ��valueMatrix������ÿһ����ɢ��������discretizationMatrixÿ�еĳ��Ȳ�ͬ������matrix����ȡ���ֺ���Щ
	 * @param valueMatrix
	 * @param n
	 */
	private Double[][] matrixDiscretization(Double[][] valueMatrix,int n)
	{
		int rowLen = valueMatrix.length;
		int colLen = valueMatrix[0].length;
		
		//ÿ�б���ѵ��������ÿ����������ɢ���ķָ��
		Double[][] discretizationMatrix = new Double[colLen][];
		//iΪ�������Ƶ�����
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
	 * ��ÿ��������ɢ��
	 * @param data
	 * @param discretizationMatrix
	 */
	public void dataDiscretization(FeatureData data,Double[][] discretizationMatrix,
			Map<String,Integer> featureNameIndex) {
			//��ǰ�е���������
			String[] names = data.getNames();
			//��ǰ�е�����ֵ
			String[] values = data.getValues();

			for (int i = 0; i < values.length; i++) {
				Integer index = featureNameIndex.get(names[i]);
				//�������ƴ���
				if(index != null)
				{

					Double newValue = valueDiscretization(discretizationMatrix[index],
							Double.parseDouble(values[i]));
					//���¸���ɢ�����ֵ
					values[i] = String.valueOf(newValue);
					
				}
			}
	}
	
	/**
	 * ÿ��ֵ������ɢ��
	 * @param discretizationArray Ϊ��С�������������
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
	 * ѡ������
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
	 * ��������,����������StackOverflowError����
	 * @param array ��������
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
	 * ����ѵ��������ɢ��
	 */
	public void disperse(List<FeatureData> trainData)
	{
		this.featureNameIndex = getFeatureNameIndex(trainData);
		Double[][] valueMatrix = getValueMatrix(trainData,this.featureNameIndex);
//		//�������
//		for (int i = 0; i < valueMatrix.length; i++) {
//			System.out.println(Arrays.toString(valueMatrix[i]));
//		}
		//��ɢ��ֵ�ľ���
		this.discretizationMatrix = matrixDiscretization(valueMatrix,this.n);
		//�ͷ���Դ
		valueMatrix = null;
//		System.out.println("discretizationMatrix.length "+discretizationMatrix.length);
//		for (int i = 0; i < discretizationMatrix.length; i++) {
//			System.out.println(Arrays.toString(discretizationMatrix[i]));
//		}
		//��������ɢ��
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
			//��ǰ�е���������
			String[] names = data.getNames();
			//��ǰ�е�����ֵ
			String[] values = data.getValues();

			for (int i = 0; i < values.length; i++) {
				System.out.print(names[i]+" "+values[i]+" ");
			}
			System.out.println("----------");
		}
	}

	
}