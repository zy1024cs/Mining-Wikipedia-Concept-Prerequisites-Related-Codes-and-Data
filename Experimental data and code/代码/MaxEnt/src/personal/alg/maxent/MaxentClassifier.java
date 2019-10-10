package personal.alg.maxent;
/** 
 * @author Chang Shu
 * @data 2015��12��10��
 */

import java.util.*;

public abstract class MaxentClassifier {	
	/** ���������� */
	protected int maxIter;
	
	/** �����ѵ������ */
	protected List<FeatureData> trainData ;
	
	/** ��¼��������Ϊ0���������� */
	protected Set<Integer> unattested ;
	
	/** �������������Ϣ */
	protected DataProcess dataProcess;
	
	/** Ȩ�� */
	protected double[] weight ;
	
	/** ��ӡ��Ϣ */
	private boolean printMessages = false;
	
	/** �㷨���� */
	private String algorithm;
	
	/** ������ֵ��ɢ����Ϣ */
	public ContinuumToDiscretization ctd ;
	
	
	public MaxentClassifier()
	{
		
	}
	
	public MaxentClassifier(FeatureDataStream es,int maxIter,String algorithm,
			int cutoff,boolean isDiscretization,int section)
	{
		this.maxIter = maxIter;
		this.algorithm = algorithm;
		this.trainData = Util.getTrainData(es);
		if(isDiscretization)
		{
			
			ctd = new ContinuumToDiscretization(section);
			//����ֵ������ɢ����trainData��values��ֵ����Ϊ��ɢ����ֵ
			ctd.disperse(trainData);
		}
		if("gis".equals(algorithm))
		{
			dataProcess = new GisDataProcess(trainData, cutoff);
		}
		else if("iis".equals(algorithm))
		{
			dataProcess = new DataProcess(trainData, cutoff);
		}
		
		
	}
	
	/**
	 * ��ʼ��Ȩ�أ���¼��������Ϊ0����������
	 * @param empiricalExpectation 
	 * @return
	 */
	protected double[] initWeight(double[] empiricalExpectation) {
		// TODO Auto-generated method stub
		
		double[] weight = new double[empiricalExpectation.length];
		for (int i = 0; i < empiricalExpectation.length; i++) {
			if(empiricalExpectation[i] == 0)
			{
				weight[i] = Double.NEGATIVE_INFINITY;
				unattested.add(i);
			}
		}
		return weight;
	}
	
	/**
	 * ����Ԥ�����������ɸ���
	 * @param trainData
	 * @return
	 */
	protected List<DictionaryProbDist> batchProbClassify(List<FeatureData> trainData)
	{
		List<DictionaryProbDist> results = new ArrayList<DictionaryProbDist>();
		for (FeatureData data : trainData) {
			results.add(probClassify(data));
		}
		return results;
	}
	
	/**
	 * ����Ԥ�����������������ܵ����
	 * @param trainData
	 * @return
	 */
	protected List<String> batchClassify(List<FeatureData> trainData)
	{
		List<String> results = new ArrayList<String>();
		for (FeatureData data : trainData) {
			results.add(classify(data));
		}
		return results;
	}
	
	/**
	 * �����������������
	 */
	protected String classify(FeatureData data) {
		// TODO Auto-generated method stub
		return probClassify(data).maxProLabel();
	}
	
	/**
	 * �������ɸ���
	 */
	public DictionaryProbDist probClassify(FeatureData data) {
		//�������Ԥ�����ĸ��ʣ�keyΪ������ƣ�valueΪ����ֵ
		Map<String,Double> probDict = new HashMap<String,Double>();
		
		for (String label : dataProcess.labels) {
			List<int[]> featureIndexByLine = dataProcess.getfeatureIndex(data.getNames(),data.getValues(),label);
			double total = 0.0;
			for (int[] featureIndex : featureIndexByLine) {
				total += weight[featureIndex[0]] * featureIndex[1] ;
			}
			probDict.put(label, total);
		}

		if (data.getLabel() == null)
		{
			return new DictionaryProbDist(probDict);
		}
		else
		{
			return new DictionaryProbDist(probDict,data.getLabel());
		}
		
	}
	
	protected void display(String s)
	{
		if (printMessages)
		{
			System.out.println(s);
		}
	}
	

	public void setPrintMessages(boolean printMessages) {
		this.printMessages = printMessages;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public ContinuumToDiscretization getContinuumToDiscretization() {
		return ctd;
	}

	public void setContinuumToDiscretization(ContinuumToDiscretization ctd) {
		this.ctd = ctd;
	}
	
	/**
	 * ѵ��
	 * @return
	 */
	protected abstract void train();
	
	/**
	 * ���㾭������
	 * @param trainData
	 * @return
	 */
	protected abstract double[] calculateEmpiricalExpectation(List<FeatureData> trainData);
	
}