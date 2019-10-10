package personal.alg.maxent;
/** 
 * @author Chang Shu
 * @data 2015年12月10日
 */

import java.util.*;

public abstract class MaxentClassifier {	
	/** 最大迭代次数 */
	protected int maxIter;
	
	/** 输入的训练数据 */
	protected List<FeatureData> trainData ;
	
	/** 记录特征次数为0的特征索引 */
	protected Set<Integer> unattested ;
	
	/** 保存特征相关信息 */
	protected DataProcess dataProcess;
	
	/** 权重 */
	protected double[] weight ;
	
	/** 打印信息 */
	private boolean printMessages = false;
	
	/** 算法名称 */
	private String algorithm;
	
	/** 保存数值离散化信息 */
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
			//将数值特征离散化，trainData里values的值将变为离散化的值
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
	 * 初始化权重，记录特征次数为0的特征索引
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
	 * 分类预测批处理生成概率
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
	 * 分类预测批处理生成最大可能的类别
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
	 * 分类生成最大概率类别
	 */
	protected String classify(FeatureData data) {
		// TODO Auto-generated method stub
		return probClassify(data).maxProLabel();
	}
	
	/**
	 * 分类生成概率
	 */
	public DictionaryProbDist probClassify(FeatureData data) {
		//保存各个预测类别的概率，key为类别名称，value为概率值
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
	 * 训练
	 * @return
	 */
	protected abstract void train();
	
	/**
	 * 计算经验期望
	 * @param trainData
	 * @return
	 */
	protected abstract double[] calculateEmpiricalExpectation(List<FeatureData> trainData);
	
}