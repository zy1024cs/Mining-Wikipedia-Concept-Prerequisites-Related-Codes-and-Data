package personal.alg.maxent;
/** 
 * @author Chang Shu
 * @data 2015��12��10��
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ModeReader {

	/**
	 * ���ļ���ȡģ��
	 * @param modeFile
	 * @return
	 */
	public static MaxentClassifier read(String modeFile)
	{
		BufferedReader reader = null;
		MaxentClassifier classifier = null;
		DataProcess dp = null;
		ContinuumToDiscretization ctd = null;

		try {
			reader = new BufferedReader(new FileReader(new File(modeFile)));
			classifier = new MaxentClassifier(){
				protected void train() {
				}
				protected double[] calculateEmpiricalExpectation(
						List<FeatureData> trainData) {
					return null;
				}};
			//��ȡ�㷨����
			String algorithm = reader.readLine();
			
			if ("gis".equals(algorithm))
			{
				dp = new GisDataProcess();
			}
			else if("iis".equals(algorithm))
			{
				dp = new DataProcess();
			}
			//��ȡ��ǩ����
			int labelSize = Integer.valueOf(reader.readLine());
			//��ȡ��ǩ����
			String[] labelText = reader.readLine().split("\t");
			if(labelSize != labelText.length)
			{
				System.err.println("��ȡ��ǩ�ĸ�������");
				System.exit(1);
			}
			//�����ǩ
			Set<String> labels = new HashSet<String>(Arrays.asList(labelText));
			//��ǩ������־
			String labelFlag = reader.readLine();
			if (!labelFlag.equals("label end"))
			{
				System.err.println("��ȡ��ǩ����");
				System.exit(1);
			}
			//Ϊgis�㷨���ȡC
			if("gis".equals(algorithm))
			{
				//��ȡ�������Ƶĳ���
				int C = Integer.valueOf(reader.readLine());
				//����C
				((GisDataProcess)dp).setC(C);
			}
			
			//��ȡ�����ĳ���
			int featureLen = Integer.valueOf(reader.readLine());
			//��ȡ��������
			String[] mappingText = reader.readLine().split("\t");
			if(featureLen != mappingText.length)
			{
				System.err.println("��ȡ������������");
				System.exit(1);
			}
			//�������ݱ����map
	        Map<String, Integer> mapping = new HashMap<String, Integer>();
	        for (int i = 0; i < mappingText.length; i++) {
	            String[] feature = mappingText[i].split(":");
	            if (feature.length == 2) {
	            	mapping.put(feature[0], Integer.valueOf(feature[1]));
	            }
	        }
	        
			//��ǩ������־
			String featureFlag = reader.readLine();
			if (!featureFlag.equals("feature end"))
			{
				System.err.println("��ȡ��������");
				System.exit(1);
			}
			
			//��ȡȨ�س���
			int weightLen = Integer.valueOf(reader.readLine());
	        //��ȡȨ��
			String[] weightText = reader.readLine().split("\t");
			if(weightLen != weightText.length)
			{
				System.err.println("��ȡȨ�صĸ�������");
				System.exit(1);
			}
			double[] weight = new double[weightText.length];
			for (int i = 0; i < weightText.length; i++) {
				weight[i] = Double.valueOf(weightText[i]);
			}
			//Ȩ�ؽ�����־
			String weightFlag = reader.readLine();
			if (!weightFlag.equals("weight end"))
			{
				System.err.println("��ȡȨ�س���");
				System.exit(1);
			}
			
			if("discretization start".equals(reader.readLine()))
			{
				ctd = new ContinuumToDiscretization();
				
				//��ȡ�������Ƹ���
				int featureNameLen = Integer.valueOf(reader.readLine());
				String[] featureNameText = reader.readLine().split("\t");
				
				if(featureNameLen != featureNameText.length)
				{
					System.err.println("��ȡ�������Ƹ�������");
					System.exit(1);
				}
				//�����������������featureNameIndex
		        Map<String, Integer> featureNameIndex = new HashMap<String, Integer>();
		        for (int i = 0; i < featureNameText.length; i++) {
		            String[] nameIndex = featureNameText[i].split(":");
		            if (nameIndex.length == 2) {
		            	featureNameIndex.put(nameIndex[0], Integer.valueOf(nameIndex[1]));
		            }
		        }
		        
		        //��ȡ��ɢ��������
		        int discretizationLen = Integer.valueOf(reader.readLine());
		        Double[][] discretizationMatrix = new Double[discretizationLen][];
		        //��ȡÿ����ɢֵ
		        for (int i = 0; i < discretizationLen; i++) {
		        	int discretizationArrayLen = Integer.valueOf(reader.readLine());
		        	String[] discretizationArrayStr = reader.readLine().split("\t");
		        	if(discretizationArrayLen != discretizationArrayStr.length)
		        	{
		        		System.err.println("��ȡÿ����ɢ��ֵ�ĸ�������");
						System.exit(1);
		        	}
		        	Double[] discretizationArray = new Double[discretizationArrayStr.length];
		        	for (int j = 0; j < discretizationArray.length; j++) {
		        		discretizationArray[j] = Double.parseDouble(discretizationArrayStr[j]);
					}
		        	discretizationMatrix[i] = discretizationArray;
				}
		        
//		        for (int i = 0; i < discretizationMatrix.length; i++) {
//		        	System.out.println(Arrays.toString(discretizationMatrix[i]));
//				}
		        
		        
		        ctd.setDiscretizationMatrix(discretizationMatrix);
				ctd.setFeatureNameIndex(featureNameIndex);
				
				//��ɢ������־
		        String discretizationFlag = reader.readLine();
				if (!discretizationFlag.equals("discretization end"))
				{
					System.err.println("��ȡ��ɢֵ����");
					System.exit(1);
				}
			}
			
			//��ȡ������־
			String endFlag = reader.readLine();
			if (!endFlag.equals("end"))
			{
				System.err.println("��ȡģ���ļ�����");
				System.exit(1);
			}
						
			dp.labels = labels;
			dp.featureLen = featureLen;
			dp.mapping = mapping;
	        classifier.weight = weight;
	        classifier.dataProcess = dp;
	        classifier.setContinuumToDiscretization(ctd);
		}
		catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		finally
		{
			try {
				if(reader != null)	
					reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return classifier;
	}
	public static void main(String[] args) {
		ModeReader.read("new_mode.txt");
	}

}