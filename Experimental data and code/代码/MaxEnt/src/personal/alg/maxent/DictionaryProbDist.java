package personal.alg.maxent;
/** 
 * @author Chang Shu
 * @data 2015��12��10��
 */

import java.util.*;
import java.util.Map.Entry;

public class DictionaryProbDist {
	/** �������Ԥ�����ĸ��ʣ�keyΪ������ƣ�valueΪ����ֵ */
	private Map<String,Double> probDict;
	/** Ϊ��ǰ��¼��label�����ڲ���Ч���� */
	private String label;


	public DictionaryProbDist(Map<String,Double> probDict)
	{
		this(probDict,null);
	}
	

	/**
	 * ��probDict�ĸ������ĸ��ʹ�һ������
	 * @param probDict
	 * @param label
	 */
	public DictionaryProbDist(Map<String,Double> probDict,String label)
	{
		this.label = label;
		double  valueSum = sumLogs(new ArrayList<Double>(probDict.values()));
//		System.out.println(valueSum);
		this.probDict = probDict;
		for (String key : probDict.keySet()) {
			this.probDict.put(key, probDict.get(key) - valueSum);
		}
		
	}
	
	/**
	 * �������ֵ��������Ӧ�ĸ���
	 * @param label
	 * @return
	 */
	public double prob(String label)
	{
		if (!probDict.containsKey(label))
		{
			return 0;
		}
		else
		{
			return Math.pow(2, probDict.get(label));
		}
	}
	
	/**
	 * map�Ÿ�����	
	 * @param map
	 * @return
	 */
	private List<Entry<String, Double>> sortMap(Map<String,Double> map)
	{
        ArrayList<Entry<String, Double>> list = new ArrayList<Entry<String, Double>>(map.entrySet());
        Collections.sort(list, new Comparator<Entry<String, Double>>() {
            @Override  
            public int compare(Entry<String, Double> arg0,Entry<String, Double> arg1) {
            	//����
            	if (arg1.getValue() > arg0.getValue()) return 1;
            	else if (arg1.getValue() < arg0.getValue()) return -1;
                return 0;  
            }  
        });  

		return list;
	}
	
	/**
	 * ȡ�����������ֵ
	 * @return
	 */
	public String maxProLabel()
	{
		return sortMap(probDict).get(0).getKey();
	}
	
	/**
	 * ���һ��������������ֵ���ܺ�
	 * @param values
	 * @return
	 */
	private double sumLogs(List<Double> values) {
		// TODO Auto-generated method stub
		if (values.size() == 0)
		{
			return Double.MIN_VALUE;
		}
		else
		{
			double addLogsMaxdiff = Math.log(Math.pow(10,-30)) / Math.log(2);
			double valueSum = values.get(0);
			for (int i = 1; i < values.size(); i++) {
				
				double x = values.get(i);
				if(valueSum < x + addLogsMaxdiff)
				{
					valueSum = x;
					continue;
				}
				if(x < valueSum + addLogsMaxdiff)
				{
					continue;
				}
				double base = Math.min(valueSum,x);
				valueSum = base + Math.log(Math.pow(2,x-base) + 
						Math.pow(2,valueSum-base)) / Math.log(2);
//				valueSum = Math.log(Math.pow(2,x) + Math.pow(2,valueSum) ) / Math.log(2);
//				System.out.println("sumLogs:"+valueSum+"  value:"+x);
			}

			return valueSum;
		}
		
	}
	
	public Map<String, Double> getProb() {
		Map<String,Double> temp = new HashMap<String,Double>();
		for (String pro : probDict.keySet()) {
			temp.put(pro,Math.pow(2, probDict.get(pro))); 
		}
//		System.out.println(temp.values());
		return temp;
	}
	
	public Map<String, Double> getProbDict() {
		return probDict;
	}


	public void setProbDict(Map<String, Double> probDict) {
		this.probDict = probDict;
	}


	public String getLabel() {
		return label;
	}


	public void setLabel(String label) {
		this.label = label;
	}
}