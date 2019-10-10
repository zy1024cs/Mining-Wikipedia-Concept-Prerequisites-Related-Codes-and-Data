package personal.alg.maxent;
/** 
 * @author Chang Shu
 * @data 2015��12��10��
 */

import java.io.*;
import java.util.Map;
import java.util.Set;

public class ModeWriter {

	/**
	 * д��ģ���ļ�
	 * @param classifier
	 * @param modeFile
	 * @return
	 */
	public static boolean write(MaxentClassifier classifier,String modeFile)
	{
		boolean flag = false;
		//�㷨����
		String algorithm = classifier.getAlgorithm();
		//Ȩ��
		double[] weight = classifier.weight;
		//������Ϣ
		DataProcess dp = classifier.dataProcess;
		Map<String,Integer> mapping = dp.mapping;
		Set<String> labels = dp.labels;
		int featureLen = dp.featureLen;
		int C = 0;
		//��ɢ����Ϣ
		ContinuumToDiscretization ctd = classifier.getContinuumToDiscretization();

		
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
					modeFile,false), "utf-8"), true);
			//�㷨����
			pw.println(algorithm);
			//��ǩ���ȼ�����
			pw.println(labels.size());
			for (String label : labels) {
				pw.print(label +"\t");
			}
			pw.println();
			pw.println("label end");
			//�������ȼ�����
			if("gis".equals(algorithm))
			{
				//���Ϊgis�㷨��д��C
				pw.println(((GisDataProcess)dp).getC());
			}
			
			pw.println(featureLen);
			for (Map.Entry<String, Integer> entry : mapping.entrySet()) {
				pw.print(entry.getKey()+":"+entry.getValue()+"\t");
			}
			pw.println();
			pw.println("feature end");
			//Ȩ�س��ȼ�����
			pw.println(weight.length);
			for (int i = 0; i < weight.length; i++) {
				pw.print(weight[i]+"\t");
			}
			pw.println();
			pw.println("weight end");
			
			//д����ɢ������
			if(ctd != null)
			{
				Map<String,Integer> featureNameIndex = ctd.getFeatureNameIndex();
				//д�뿪ʼ��־
				pw.println("discretization start");
				//д���������Ƹ���
				pw.println(featureNameIndex.size());
				//д��������������
				for (Map.Entry<String, Integer> entry : featureNameIndex.entrySet()) {
					pw.print(entry.getKey()+":"+entry.getValue()+"\t");
				}
				pw.println();
				
				Double[][] discretizationMatrix = ctd.getDiscretizationMatrix();
				//д����ɢ������������
				pw.println(discretizationMatrix.length);
				for (int i = 0; i < discretizationMatrix.length; i++) {
					//д��ÿ����ɢֵ����
					pw.println(discretizationMatrix[i].length);
					for (int j = 0; j < discretizationMatrix[i].length; j++) {
						pw.print(discretizationMatrix[i][j]+"\t");
					}
					pw.println();
				}
				pw.println("discretization end");
				
			}
			else
			{
				pw.println("no discretization");
			}
			pw.println("end");
			
			flag = true;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			if (pw != null)
				pw.close();
		}

		return flag;
	}

}