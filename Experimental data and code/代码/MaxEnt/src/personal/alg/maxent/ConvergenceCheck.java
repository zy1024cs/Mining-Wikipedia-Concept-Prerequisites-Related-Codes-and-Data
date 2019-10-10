package personal.alg.maxent;
/** 
 * @author Chang Shu
 * @data 2015年12月10日
 */
import java.util.List;

public class ConvergenceCheck {
	
	private static int iter = 1;
	//当前对数似然值
	public static Double logLikelihoodValue = null;
	//logLikelihood值的最小差异值
	private static Double logLikelihoodDiff = 0.001;  //0.001
	//当前准确性
	public static Double accuracyValue = null;
	//最小概率差异值
	private static Double accuracyDiff = 0.0001;  //0.0001
	/**
	 * 迭代终止条件
	 * @return
	 */
	public static boolean check(MaxentClassifier classifier,List<FeatureData> trainData)
	{
		iter ++;
		if (iter >= classifier.maxIter)
		{
			return true;
		}
		
		//判断似然值收敛
		Double newLogLikelihoodValue = Util.logLikelihood(classifier, trainData);
		//小于最小差异就认为收敛
		if(logLikelihoodValue != null && 
				(newLogLikelihoodValue - logLikelihoodValue) < logLikelihoodDiff)
		{
			return true;
		}
		logLikelihoodValue = newLogLikelihoodValue;
		
		//判断概率收敛
		Double newAccuracyValue = Util.accuracy(classifier, trainData);
		//小于最小概率就认为收敛
		if(accuracyValue != null && 
				(newAccuracyValue - accuracyValue) < accuracyDiff)
		{
			return true;
		}
		accuracyValue = newAccuracyValue;
		
		
		return false;
	}
}