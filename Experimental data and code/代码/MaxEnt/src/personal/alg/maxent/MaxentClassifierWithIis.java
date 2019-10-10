package personal.alg.maxent;
/** 
 * @author Chang Shu
 * @data 2015年12月10日
 */

import java.util.*;

public class MaxentClassifierWithIis extends MaxentClassifier {
	
	public MaxentClassifierWithIis(FeatureDataStream es,String algorithm,int maxIter,
			int cutoff,boolean isDiscretization,int section ) {
		super(es, maxIter,algorithm,cutoff,isDiscretization,section);
		this.unattested = new HashSet<Integer>();
	}

	/**
	 * 训练数据
	 */
	public void train() {
		double[] empiricalFfreq = calculateEmpiricalExpectation(trainData);
		weight = initWeight(empiricalFfreq);
		Map<Integer,Integer> nfMap = calculateNfmap(trainData);
		int[] nfarray = getNfarray(nfMap);
		int i = 1;
		display("Iteration	Log Likelihood	Accuracy");
		while(true)
		{
			//除第一次之外，后面算似然值在ConvergenceCheck类里计算
			double LikelihoodValue = ConvergenceCheck.logLikelihoodValue != null ?
					ConvergenceCheck.logLikelihoodValue : Util.logLikelihood(this, trainData);
			double accuracyValue = ConvergenceCheck.accuracyValue != null ?
					ConvergenceCheck.accuracyValue : Util.accuracy(this, trainData);
			display(i++ +"\t"+ LikelihoodValue+"\t"+accuracyValue);
			double[] delta =  calculateDeltas(trainData,empiricalFfreq,nfMap,nfarray);
			updataWeight(delta);
			
			if (ConvergenceCheck.check(this, trainData))
			{
				break;
			}
		}
		
		double LikelihoodValue = Util.logLikelihood(this, trainData);
		double accuracyValue = Util.accuracy(this, trainData);
		display("final\t"+LikelihoodValue+"\t"+accuracyValue);
	}
	
	/**
	 * 更新权重
	 * @param delta
	 */
	private void updataWeight(double[] delta) {
		for (int i = 0; i < weight.length; i++) {
			weight[i] += delta[i];
		}
	}
	
	/**
	 * 计算每句特征出现次数
	 * @param trainData
	 * @return
	 */
	private Map<Integer,Integer> calculateNfmap(List<FeatureData> trainData)
	{
		Map<Integer,Integer> nfMap = new HashMap<Integer,Integer>();
		Set<Integer> nfSet = new HashSet<Integer>();
		int i = 0;
		for (FeatureData data : trainData) {
			for (String label : dataProcess.labels) {
				List<int[]> featureIndexByLine = dataProcess.getfeatureIndex(data.getNames(),data.getValues(),label);
				int featureCount = 0;
				for (int[] featureIndex : featureIndexByLine) {
					featureCount += featureIndex[1] ;
				}
				nfSet.add(featureCount);
			}
		}
		for (Integer featureCount : nfSet) {
			nfMap.put(featureCount, i ++);
		}
		return nfMap;
	}
	
	/**
	 * nfMap转化为数组
	 * @param nfMap
	 * @return
	 */
	private int[] getNfarray(Map<Integer,Integer> nfMap)
	{
		int[] nfarray = new int[nfMap.size()];
		for (Map.Entry<Integer, Integer> entry : nfMap.entrySet()) {
			nfarray[entry.getValue()] = entry.getKey();
		}
		return nfarray;
	}
	
	/**
	 * 计算每行特征次数的期望
	 * @param nfMap
	 * @return
	 */
	private double[][] getExpectationMatrixByCount(Map<Integer,Integer> nfMap)
	{
		double[][] expectationMatrixByCount = new double[nfMap.size()][dataProcess.featureLen];
		for (FeatureData data : trainData) {
			DictionaryProbDist pDist = probClassify(data);
			for (String label : dataProcess.labels) {
				double prob = pDist.prob(label);
				//特征出现次数
				int nf = 0;
				for (int[] featureIndex : dataProcess.getfeatureIndex(data.getNames(),data.getValues(),label)) {
					nf += featureIndex[1] ;
				}
				//更新矩阵
				for (int[] featureIndex : dataProcess.getfeatureIndex(data.getNames(),data.getValues(),label)) {
					expectationMatrixByCount[nfMap.get(nf)][featureIndex[0]] += prob * featureIndex[1] ;
				}
			}
		}
		
		int trainDataLen = trainData.size();
		for (int i = 0; i < expectationMatrixByCount.length; i++) {
			for (int j = 0; j < expectationMatrixByCount[0].length; j++) {
				expectationMatrixByCount[i][j] /=  trainDataLen;
			}
		}
		return expectationMatrixByCount;
	}
	
	/**
	 * 使用牛顿法求解参数
	 * @param delta 更新的参数
	 * @param empiricalExpectation 
	 * @param expectationMatrixByCount 
	 * @param nfarray
	 */
	private void newTonSolve(double[] delta,double[] empiricalExpectation,
			double[][] expectationMatrixByCount,int[] nfarray)
	{
		//牛顿法收敛值,可自行设置
		double newtonConverge = Math.pow(10, -12);
		//牛顿法最大迭代次数,可自行设置
		int maxNewton = 300;
		//牛顿法求解
		for (int i = 0; i < maxNewton; i++) {
			//模型期望
			double[] modeExpectation = new double[delta.length];
			//经验期望-模型期望的导数df
			double[] df = new double[delta.length];
			for (int m = 0; m < delta.length; m++) {
				for (int n = 0; n < nfarray.length; n++) {
					double expNfDelta = Math.pow(2,delta[m] * nfarray[n]);
					modeExpectation[m] += expNfDelta * expectationMatrixByCount[n][m];
					df[m] += nfarray[n] * expNfDelta * expectationMatrixByCount[n][m];
				}
			}
			//避免除数为0
			for (int fid : unattested) {
				df[fid] += 1; 
			}
			//牛顿法迭代公式
			for (int j = 0; j < delta.length; j++) {
				delta[j] -= (empiricalExpectation[j] - modeExpectation[j]) / -df[j];
			}
			
			//检查近似值是否符合要求
			double nError = 0.0;
			double a = 0.0;
			double b = 0.0;
			for (int j = 0; j < delta.length; j++) {
				a += Math.abs(empiricalExpectation[j] - modeExpectation[j]);
				b += Math.abs(delta[j]);
			}
			nError = a / b;
			if( nError < newtonConverge)
			{
				break;
			}
			i ++;
		}
	}
	
	/**
	 * 计算delta
	 * @param trainData 训练数据
	 * @param empiricalExpectation 经验期望
	 * @param nfMap 每行特征出现次数map
	 * @param nfarray map转数组
	 * @return delta值
	 */
	private double[] calculateDeltas(List<FeatureData> trainData,double[] empiricalExpectation,
			Map<Integer,Integer> nfMap,int[] nfarray)
	{
		double[] delta = new double[dataProcess.featureLen];
		//delta赋初值
		for (int i = 0; i < delta.length; i++) {
			delta[i] = 1.0;
		}
		//计算每行特征次数的期望
		double[][] expectationMatrixByCount = getExpectationMatrixByCount(nfMap);
		
		//更新delta
		newTonSolve(delta,empiricalExpectation,expectationMatrixByCount,nfarray);
		
		return delta;
		
	}
	
	/**
	 * 计算经验期望
	 * @param trainData 
	 */
	protected double[] calculateEmpiricalExpectation(List<FeatureData> trainData) {
		double[] fcount = new double[dataProcess.featureLen];
		for (FeatureData data : trainData) {
			for (int[] featureIndex : dataProcess.getfeatureIndex(data.getNames(),data.getValues(),data.getLabel())) {
				fcount[featureIndex[0]] += featureIndex[1];
			}
		}
		int trainDataLen = trainData.size();
		for (int i = 0; i < fcount.length; i++) {
			fcount[i] /= trainDataLen;
		}
		return fcount;
	}

}
