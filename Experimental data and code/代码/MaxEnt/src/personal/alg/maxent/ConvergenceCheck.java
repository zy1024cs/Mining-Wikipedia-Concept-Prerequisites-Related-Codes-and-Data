package personal.alg.maxent;
/** 
 * @author Chang Shu
 * @data 2015��12��10��
 */
import java.util.List;

public class ConvergenceCheck {
	
	private static int iter = 1;
	//��ǰ������Ȼֵ
	public static Double logLikelihoodValue = null;
	//logLikelihoodֵ����С����ֵ
	private static Double logLikelihoodDiff = 0.001;  //0.001
	//��ǰ׼ȷ��
	public static Double accuracyValue = null;
	//��С���ʲ���ֵ
	private static Double accuracyDiff = 0.0001;  //0.0001
	/**
	 * ������ֹ����
	 * @return
	 */
	public static boolean check(MaxentClassifier classifier,List<FeatureData> trainData)
	{
		iter ++;
		if (iter >= classifier.maxIter)
		{
			return true;
		}
		
		//�ж���Ȼֵ����
		Double newLogLikelihoodValue = Util.logLikelihood(classifier, trainData);
		//С����С�������Ϊ����
		if(logLikelihoodValue != null && 
				(newLogLikelihoodValue - logLikelihoodValue) < logLikelihoodDiff)
		{
			return true;
		}
		logLikelihoodValue = newLogLikelihoodValue;
		
		//�жϸ�������
		Double newAccuracyValue = Util.accuracy(classifier, trainData);
		//С����С���ʾ���Ϊ����
		if(accuracyValue != null && 
				(newAccuracyValue - accuracyValue) < accuracyDiff)
		{
			return true;
		}
		accuracyValue = newAccuracyValue;
		
		
		return false;
	}
}