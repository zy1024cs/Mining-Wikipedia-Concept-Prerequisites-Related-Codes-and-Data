package personal.alg.maxent;
/** 
 * @author Chang Shu
 * @data 2015��12��10��
 */

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GisDataProcess extends DataProcess {
	/** �������Ƹ���+1 */
	private int C ;

	public GisDataProcess()
	{
		
	}

    public GisDataProcess(List<FeatureData> trainData,int cutoff) {
        super(trainData,cutoff);
		this.C = extractFeatureC(mapping);
	}
	
	/**
	 * ����ÿ�м�¼������Ϣ,������У������
	 */
	public List<int[]> getfeatureIndex(String[] names,String[] values,String label) {
		List<int[]> encoding = super.getfeatureIndex(names,values,label);
		//��ǰ�е���������
		int total = 0 ;
		for (int[] e : encoding) {
			total += e[1];
		}
		if (total >= this.C)
		{
			System.err.println("Correction feature is not high enough!");
		}
		//����gis������У����������ģ��Ԥ��������Ӱ�죬����֤��iis��Berger˵��ģ������Ҳû�ã�����ȥ��Ҳ���ԣ���Ϊ�˱���ԭ��ԭζ�������Ǽ�����
		encoding.add(new int[]{featureLen,this.C - total});
		return encoding;
	}
	
	/**
	 * ������������ǩ���ϣ������������Ƹ���C
	 */
	private int extractFeatureC(Map<String,Integer> mapping)
	{
		//������������
		Set<String> featureNames = new HashSet<String>();
		for (Map.Entry<String, Integer> entry : mapping.entrySet()) {
			featureNames.add(entry.getKey().split(" ")[0]);
		}
		//��������������+1��ֹΪ0
        return featureNames.size() + 1;
	}
	
	
	public int getC() {
		return C;
	}

	public void setC(int c) {
		C = c;
	}
	
}