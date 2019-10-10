package personal.alg.maxent;
/** 
 * @author Chang Shu
 * @data 2015年12月10日
 */

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GisDataProcess extends DataProcess {
	/** 特征名称个数+1 */
	private int C ;

	public GisDataProcess()
	{
		
	}

    public GisDataProcess(List<FeatureData> trainData,int cutoff) {
        super(trainData,cutoff);
		this.C = extractFeatureC(mapping);
	}
	
	/**
	 * 返回每行记录特征信息,并增加校验特征
	 */
	public List<int[]> getfeatureIndex(String[] names,String[] values,String label) {
		List<int[]> encoding = super.getfeatureIndex(names,values,label);
		//当前行的特征个数
		int total = 0 ;
		for (int[] e : encoding) {
			total += e[1];
		}
		if (total >= this.C)
		{
			System.err.println("Correction feature is not high enough!");
		}
		//用于gis，增加校验特征，对模型预测无明显影响，后来证明iis的Berger说对模型收敛也没用，所以去掉也可以，但为了保持原滋原味，俺还是加上了
		encoding.add(new int[]{featureLen,this.C - total});
		return encoding;
	}
	
	/**
	 * 保存特征及标签集合，保存特征名称个数C
	 */
	private int extractFeatureC(Map<String,Integer> mapping)
	{
		//保存特征名称
		Set<String> featureNames = new HashSet<String>();
		for (Map.Entry<String, Integer> entry : mapping.entrySet()) {
			featureNames.add(entry.getKey().split(" ")[0]);
		}
		//名称特征个数，+1防止为0
        return featureNames.size() + 1;
	}
	
	
	public int getC() {
		return C;
	}

	public void setC(int c) {
		C = c;
	}
	
}