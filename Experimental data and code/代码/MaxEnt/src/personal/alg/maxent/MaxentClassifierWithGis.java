package personal.alg.maxent;
/** 
 * @author Chang Shu
 * @data 2015年12月10日
 */

import java.util.HashSet;
import java.util.List;

public class MaxentClassifierWithGis extends MaxentClassifier {
	
	public MaxentClassifierWithGis()
	{
		
	}
	
	public MaxentClassifierWithGis(FeatureDataStream es,String algorithm,int maxIter,
			int cutoff,boolean isDiscretization,int section) {
		// TODO Auto-generated constructor stub
		super(es,maxIter,algorithm,cutoff,isDiscretization,section);
		this.unattested = new HashSet<Integer>();
	}
	

	
	/**
	 * 计算经验期望，没有像标准公式推导一样每个特征除以总特征数量
	 * @param trainData
	 * @return
	 */
	protected double[] calculateEmpiricalExpectation(List<FeatureData> trainData)
	{
		double[] fcount = new double[dataProcess.featureLen + 1];
		for (FeatureData data : trainData) {
			for (int[] featureIndex : dataProcess.getfeatureIndex(data.getNames(),data.getValues(),data.getLabel())) {
				fcount[featureIndex[0]] += featureIndex[1];
			}
		}
		
		return fcount;
	}
	
	/**
	 * 计算模型期望
	 * @param trainData
	 * @return
	 */
	private double[] calculateEstimatedFcount(List<FeatureData> trainData)
	{
		double[] fcount = new double[dataProcess.featureLen + 1];
		for (FeatureData data : trainData) {
			//计算计算p(y|x)
			DictionaryProbDist pDist = probClassify(data);
			for (String label : dataProcess.labels) {
				double prob = pDist.prob(label);
				for (int[] featureIndex : dataProcess.getfeatureIndex(data.getNames(),data.getValues(),label)) {
					fcount[featureIndex[0]] += featureIndex[1] * prob;
				}
			}
		}
		//避免log(0)
		for (int fid : unattested) {
			fcount[fid] += 1; 
		}
		
		return fcount;
	}
	
	/**
	 * 更新权重
	 * @param empiricalFcount
	 * @param estimatedFcount
	 * @param cinv
	 */
	private void updataWeight(double[] empiricalFcount,	double[] estimatedFcount,double cinv) 
	{
		// TODO Auto-generated method stub
		for (int i = 0; i < weight.length; i++) {
			weight[i] += (Math.log(empiricalFcount[i]) - Math.log(estimatedFcount[i])) 
					/ Math.log(2) * cinv;
		}
	}
	
	/**
	 * 训练最大熵模型
	 */
	public void train()
	{
		//cinv控制学习速率，越大收敛更快，但过大了就在极值附近徘徊。
		double cinv = 1.0 / ((GisDataProcess)dataProcess).getC();
		double[] empiricalFcount = calculateEmpiricalExpectation(trainData);
		weight = initWeight(empiricalFcount);
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
			double[] estimatedFcount = calculateEstimatedFcount(trainData);
			updataWeight(empiricalFcount,estimatedFcount,cinv);
			if (ConvergenceCheck.check(this, trainData))
			{
				break;
			}
		}
		
		double LikelihoodValue = Util.logLikelihood(this, trainData);
		double accuracyValue = Util.accuracy(this, trainData);
		display("final\t"+LikelihoodValue+"\t"+accuracyValue);
	}

}