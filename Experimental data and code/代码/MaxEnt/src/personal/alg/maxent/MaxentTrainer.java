package personal.alg.maxent;
/** 
 * @author Chang Shu
 * @data 2015年12月10日
 */

public class MaxentTrainer {
	//默认算法
	private static String algorithm = "iis";
	//默认最大迭代次数
	private static int maxIter = 100;
	//分类器
	private static MaxentClassifier classifier = null;
	//是否将数值类型离散化
	private static boolean isDiscretization = true;
	//划分离散区间段数
	private static int section = 5000;
	//是否打印信息
	private static boolean printMessages = true;

	/**
	 * 训练数据
	 * @param es 输入数据流
	 * @return
	 */
	public static MaxentClassifier train(FeatureDataStream es)
	{
		return train(es,algorithm,maxIter,isDiscretization,section);
	}
	
	/**
	 * 训练数据
	 * @param es 输入数据流
	 * @param algorithm 算法名称
	 * @return
	 */
	public static MaxentClassifier train(FeatureDataStream es,String algorithm)
	{
		return train(es,algorithm,maxIter,isDiscretization,section);
	}
	
	
	/**
	 * 训练数据
	 * @param es 输入数据流
	 * @param algorithm 算法名称，目前支持gis和iis
	 * @param maxIter 迭代次数
	 * @return
	 */
	public static MaxentClassifier train(FeatureDataStream es,String algorithm,int maxIter)
	{
		return train(es,algorithm,maxIter,isDiscretization,section);
	}
	
	public static MaxentClassifier train(FeatureDataStream es,String algorithm,int maxIter,
			boolean isDiscretization)
	{
		return train(es,algorithm,maxIter,isDiscretization,section);
	}
	
	/**
	 * 
	 * @param es 输入数据流(TrainData)
	 * @param algorithm 算法次数
	 * @param maxIter 迭代次数
	 * @param isDiscretization true将数值离散化
	 * @param section 数值离散化的区间数量，只有对isDiscretization=true 有效
	 * @return
	 */
	public static MaxentClassifier train(FeatureDataStream es,String algorithm,
			int maxIter,boolean isDiscretization,int section)
	{
		algorithm = algorithm.toLowerCase();
		if ("gis".equals(algorithm))
		{
			//cutoff 平滑模型的参数，过滤特征出现次数，论文中认为某些低频词也重要，所以cutoff基本不需要了，默认为0不过滤
			classifier = new MaxentClassifierWithGis(es, "gis",maxIter,0,isDiscretization,section);
		}
		else if("iis".equals(algorithm))
		{
			classifier = new MaxentClassifierWithIis(es, "iis",maxIter,0,isDiscretization,section);
		}
		//是否打印信息
		classifier.setPrintMessages(printMessages);
		//训练
		classifier.train();
		return classifier;
	}
	


}