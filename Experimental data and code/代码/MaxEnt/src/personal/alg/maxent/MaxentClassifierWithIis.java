package personal.alg.maxent;
/** 
 * @author Chang Shu
 * @data 2015��12��10��
 */

import java.util.*;

public class MaxentClassifierWithIis extends MaxentClassifier {
	
	public MaxentClassifierWithIis(FeatureDataStream es,String algorithm,int maxIter,
			int cutoff,boolean isDiscretization,int section ) {
		super(es, maxIter,algorithm,cutoff,isDiscretization,section);
		this.unattested = new HashSet<Integer>();
	}

	/**
	 * ѵ������
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
			//����һ��֮�⣬��������Ȼֵ��ConvergenceCheck�������
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
	 * ����Ȩ��
	 * @param delta
	 */
	private void updataWeight(double[] delta) {
		for (int i = 0; i < weight.length; i++) {
			weight[i] += delta[i];
		}
	}
	
	/**
	 * ����ÿ���������ִ���
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
	 * nfMapת��Ϊ����
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
	 * ����ÿ����������������
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
				//�������ִ���
				int nf = 0;
				for (int[] featureIndex : dataProcess.getfeatureIndex(data.getNames(),data.getValues(),label)) {
					nf += featureIndex[1] ;
				}
				//���¾���
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
	 * ʹ��ţ�ٷ�������
	 * @param delta ���µĲ���
	 * @param empiricalExpectation 
	 * @param expectationMatrixByCount 
	 * @param nfarray
	 */
	private void newTonSolve(double[] delta,double[] empiricalExpectation,
			double[][] expectationMatrixByCount,int[] nfarray)
	{
		//ţ�ٷ�����ֵ,����������
		double newtonConverge = Math.pow(10, -12);
		//ţ�ٷ�����������,����������
		int maxNewton = 300;
		//ţ�ٷ����
		for (int i = 0; i < maxNewton; i++) {
			//ģ������
			double[] modeExpectation = new double[delta.length];
			//��������-ģ�������ĵ���df
			double[] df = new double[delta.length];
			for (int m = 0; m < delta.length; m++) {
				for (int n = 0; n < nfarray.length; n++) {
					double expNfDelta = Math.pow(2,delta[m] * nfarray[n]);
					modeExpectation[m] += expNfDelta * expectationMatrixByCount[n][m];
					df[m] += nfarray[n] * expNfDelta * expectationMatrixByCount[n][m];
				}
			}
			//�������Ϊ0
			for (int fid : unattested) {
				df[fid] += 1; 
			}
			//ţ�ٷ�������ʽ
			for (int j = 0; j < delta.length; j++) {
				delta[j] -= (empiricalExpectation[j] - modeExpectation[j]) / -df[j];
			}
			
			//������ֵ�Ƿ����Ҫ��
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
	 * ����delta
	 * @param trainData ѵ������
	 * @param empiricalExpectation ��������
	 * @param nfMap ÿ���������ִ���map
	 * @param nfarray mapת����
	 * @return deltaֵ
	 */
	private double[] calculateDeltas(List<FeatureData> trainData,double[] empiricalExpectation,
			Map<Integer,Integer> nfMap,int[] nfarray)
	{
		double[] delta = new double[dataProcess.featureLen];
		//delta����ֵ
		for (int i = 0; i < delta.length; i++) {
			delta[i] = 1.0;
		}
		//����ÿ����������������
		double[][] expectationMatrixByCount = getExpectationMatrixByCount(nfMap);
		
		//����delta
		newTonSolve(delta,empiricalExpectation,expectationMatrixByCount,nfarray);
		
		return delta;
		
	}
	
	/**
	 * ���㾭������
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
