package personal.alg.maxent;
/**
 * @author Chang Shu
 * @data 2015��12��10��
 */
import java.util.*;

public class DataProcess {
    /** ����������keyΪ�������ƣ�valueΪ�������� */
    protected Map<String,Integer> mapping;
    /** ��ǩ���� */
    protected Set<String> labels;
    /** �������� */
    protected int featureLen;
    /** ����������Ӧ���������� */
    protected String[] featureIndexName;

    public DataProcess()
    {

    }


    public DataProcess(List<FeatureData> trainData,int cutoff) {
        //������������,String �ֱ𱣴�:ѵ���ֶ�����+" "+���ƶ�Ӧֵ+" "+��ǩ
        mapping = new HashMap<String,Integer>();
        //�����ǩ����
        labels = new HashSet<String>();
        //���� mapping �� labels ֵ
        extractFeature(trainData, cutoff, mapping, labels);
        featureLen = mapping.size();
        featureIndexName = toIndexedStringArray(mapping);
    }

    /**
     * ������������ǩ����
     * @param trainData
     * @param cutoff
     * @return
     */
    private void extractFeature(List<FeatureData> trainData,int cutoff,Map<String,Integer> mapping,Set<String> labels)
    {

        //��������ѵ�������¸���ֵ�ĸ�����String ����:ѵ���ֶ�����+" "+���ƶ�Ӧֵ
        Map<String,Integer> count = new HashMap<String,Integer>();

        for (FeatureData data : trainData) {
            //ÿ��������������
            String[] names = data.getNames();
            //ÿ����������ֵ
            String[] values = data.getValues();
            //ÿ�б�ǩ
            String label = data.getLabel();
            //��ǩ����
            labels.add(label);
            for (int i = 0; i < names.length; i++) {
                String nameWithValue = names[i] + " " + values[i];
                count.put(nameWithValue,(count.get(nameWithValue) == null ? 0 : count.get(nameWithValue)) + 1);
                //����ĳ����Ƶ�Ĳ���������
                if(count.get(nameWithValue) >= cutoff )
                {
                    String singleFeature = nameWithValue + " " + label;
                    if (! mapping.containsKey(singleFeature))
                    {
                        //������������
                        mapping.put(singleFeature, mapping.size());
                    }
                }
            }
        }
    }

    /**
     * ����ÿ�м�¼������Ϣ��int[] ����Ϊ2��[0]Ϊ����������[1]Ϊ����
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
//		//ÿ��������������
//		String[] names = data.getNames();
//		//ÿ����������ֵ
//		String[] values = data.getValues();
//		//ÿ�б�ǩ
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