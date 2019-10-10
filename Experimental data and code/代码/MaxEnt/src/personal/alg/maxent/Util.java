package personal.alg.maxent;
/** 
 * @author Chang Shu
 * @data 2015年12月10日
 */
import java.util.ArrayList;
import java.util.List;

public class Util {
	
	/**
	 * 计算准确率
	 * @param classifier
	 * @param trainData
	 * @return
	 */
	public static double accuracy(MaxentClassifier classifier,List<FeatureData> trainData)
	{
		int totalCount = 0;
		int correctCount = 0 ;
		List<DictionaryProbDist> results = classifier.batchProbClassify(trainData);
		for (DictionaryProbDist pDist : results) {
			String maxLabel = pDist.maxProLabel();
			if (maxLabel.equals(pDist.getLabel()))
			{
				correctCount ++ ;
			}
			totalCount ++;
		}
		if (totalCount > 0)
		{
			return (double)correctCount / totalCount;
		}
		else
		{
			return 0;
		}
	}
	
	/**
	 * 计算似然值
	 * @param classifier
	 * @param trainData
	 * @return
	 */
	public static double logLikelihood(MaxentClassifier classifier,List<FeatureData> trainData) {
		// TODO Auto-generated method stub
		int totalCount = 0;
		double d = 0.0; 
		List<DictionaryProbDist> results = classifier.batchProbClassify(trainData);
		for (DictionaryProbDist pDist : results) {
			d += pDist.prob(pDist.getLabel());
			totalCount ++ ;
		}
		return Math.log(d / totalCount);
	}
	
	/**
	 * 返回特征List列表
	 * @param es 输入流
	 * @return
	 */
	public static List<FeatureData> getTrainData(FeatureDataStream es)
	{
		List<FeatureData> dataList = new ArrayList<FeatureData>();
		while(es.hasNext())
		{
			dataList.add(es.next());
		}
		return dataList;
	}
	
	
	/**
	 * 解析字符串数值生成存储特征的数据类
	 * @param featureStr
	 * @return
	 */
	public static FeatureData createFeatureData(String featureStr) {
		int lastSpace = featureStr.lastIndexOf(' ');
		if (lastSpace == -1) 
			return null;
		else {
			String[] contexts = featureStr.substring(0,lastSpace).split("\\s+");
			String[] values = parseContexts(contexts);
			FeatureData data = new FeatureData();
			data.setNames(contexts);
			data.setValues(values);
			data.setLabel(featureStr.substring(lastSpace+1));
			return data;
		}
	}
  
  /**
   * 分别保存特征名称和特征值
   * @param contexts
   * @return
   */
	private static String[] parseContexts(String[] contexts) {
		boolean hasValue = false;
		String[] values = new String[contexts.length]; 
		for (int ci = 0; ci < contexts.length; ci++) {
			int ei = contexts[ci].lastIndexOf("=");
			if (ei > 0 && ei+1 < contexts[ci].length()) {
				values[ci] = contexts[ci].substring(ei+1);
				contexts[ci] = contexts[ci].substring(0,ei);
				hasValue = true;
			}
			else {
				values[ci] = "1";
			}
		}
		if (!hasValue) {
			values = null;
		}
		return values;
	 }
}