package personal.alg.maxent;
/**
 * @author Chang Shu
 * @data 2015年12月10日
 */
import java.util.*;

public class DataProcess {
    /** 保存特征，key为特征名称，value为特征索引 */
    protected Map<String,Integer> mapping;
    /** 标签集合 */
    protected Set<String> labels;
    /** 特征长度 */
    protected int featureLen;
    /** 特征索引对应的特征名称 */
    protected String[] featureIndexName;

    public DataProcess()
    {

    }


    public DataProcess(List<FeatureData> trainData,int cutoff) {
        //保存所有特征,String 分别保存:训练字段名称+" "+名称对应值+" "+标签
        mapping = new HashMap<String,Integer>();
        //保存标签集合
        labels = new HashSet<String>();
        //生成 mapping 及 labels 值
        extractFeature(trainData, cutoff, mapping, labels);
        featureLen = mapping.size();
        featureIndexName = toIndexedStringArray(mapping);
    }

    /**
     * 保存特征及标签集合
     * @param trainData
     * @param cutoff
     * @return
     */
    private void extractFeature(List<FeatureData> trainData,int cutoff,Map<String,Integer> mapping,Set<String> labels)
    {

        //保存输入训练名称下各个值的个数，String 保存:训练字段名称+" "+名称对应值
        Map<String,Integer> count = new HashMap<String,Integer>();

        for (FeatureData data : trainData) {
            //每行所有特征名称
            String[] names = data.getNames();
            //每行所有特征值
            String[] values = data.getValues();
            //每行标签
            String label = data.getLabel();
            //标签集合
            labels.add(label);
            for (int i = 0; i < names.length; i++) {
                String nameWithValue = names[i] + " " + values[i];
                count.put(nameWithValue,(count.get(nameWithValue) == null ? 0 : count.get(nameWithValue)) + 1);
                //大于某个词频的才算做特征
                if(count.get(nameWithValue) >= cutoff )
                {
                    String singleFeature = nameWithValue + " " + label;
                    if (! mapping.containsKey(singleFeature))
                    {
                        //放入特征索引
                        mapping.put(singleFeature, mapping.size());
                    }
                }
            }
        }
    }

    /**
     * 返回每行记录特征信息，int[] 长度为2，[0]为特征索引，[1]为次数
     */
    public List<int[]> getfeatureIndex(String[] names,String[] values,String label) {
        List<int[]> encoding = new ArrayList<int[]>();
        for (int i = 0; i < names.length; i++) {
            String singleFeature = names[i] + " " + values[i] + " " + label;
            if (mapping.containsKey(singleFeature))
            {
                encoding.add(new int[]{mapping.get(singleFeature),1});
            }
        }

        return encoding;
    }

//	public List<int[]> encode(TrainData data) {
//		// TODO Auto-generated method stub
//		List<int[]> encoding = new ArrayList<int[]>();
//		//每行所有特征名称
//		String[] names = data.getNames();
//		//每行所有特征值
//		String[] values = data.getValues();
//		//每行标签
//		String label = data.getLabel();
//		for (int i = 0; i < names.length; i++) {
//			String singleFeature = names[i] + " " + values[i] + " " + label;
//			if (mapping.containsKey(singleFeature))
//			{
//				encoding.add(new int[]{mapping.get(singleFeature),1});
//			}
//		}
//		
//		return encoding;
//	}

    private String[] toIndexedStringArray(Map<String,Integer> labelToIndexMap) {
        final String[] array = new String[labelToIndexMap.size()];
        for (String label : labelToIndexMap.keySet()) {
            array[labelToIndexMap.get(label)] = label;
        }
        return array;
    }
}