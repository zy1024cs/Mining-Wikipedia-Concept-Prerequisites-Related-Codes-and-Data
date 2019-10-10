package personal.alg.maxent;
/** 
 * @author Chang Shu
 * @data 2015年12月10日
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ModeReader {

	/**
	 * 从文件读取模型
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
			//读取算法名称
			String algorithm = reader.readLine();
			
			if ("gis".equals(algorithm))
			{
				dp = new GisDataProcess();
			}
			else if("iis".equals(algorithm))
			{
				dp = new DataProcess();
			}
			//读取标签长度
			int labelSize = Integer.valueOf(reader.readLine());
			//读取标签内容
			String[] labelText = reader.readLine().split("\t");
			if(labelSize != labelText.length)
			{
				System.err.println("读取标签的个数出错");
				System.exit(1);
			}
			//保存标签
			Set<String> labels = new HashSet<String>(Arrays.asList(labelText));
			//标签结束标志
			String labelFlag = reader.readLine();
			if (!labelFlag.equals("label end"))
			{
				System.err.println("读取标签出错");
				System.exit(1);
			}
			//为gis算法则读取C
			if("gis".equals(algorithm))
			{
				//读取特征名称的长度
				int C = Integer.valueOf(reader.readLine());
				//保存C
				((GisDataProcess)dp).setC(C);
			}
			
			//读取特征的长度
			int featureLen = Integer.valueOf(reader.readLine());
			//读取特征内容
			String[] mappingText = reader.readLine().split("\t");
			if(featureLen != mappingText.length)
			{
				System.err.println("读取特征个数出错");
				System.exit(1);
			}
			//特征内容保存进map
	        Map<String, Integer> mapping = new HashMap<String, Integer>();
	        for (int i = 0; i < mappingText.length; i++) {
	            String[] feature = mappingText[i].split(":");
	            if (feature.length == 2) {
	            	mapping.put(feature[0], Integer.valueOf(feature[1]));
	            }
	        }
	        
			//标签结束标志
			String featureFlag = reader.readLine();
			if (!featureFlag.equals("feature end"))
			{
				System.err.println("读取特征出错");
				System.exit(1);
			}
			
			//读取权重长度
			int weightLen = Integer.valueOf(reader.readLine());
	        //读取权重
			String[] weightText = reader.readLine().split("\t");
			if(weightLen != weightText.length)
			{
				System.err.println("读取权重的个数出错");
				System.exit(1);
			}
			double[] weight = new double[weightText.length];
			for (int i = 0; i < weightText.length; i++) {
				weight[i] = Double.valueOf(weightText[i]);
			}
			//权重结束标志
			String weightFlag = reader.readLine();
			if (!weightFlag.equals("weight end"))
			{
				System.err.println("读取权重出错");
				System.exit(1);
			}
			
			if("discretization start".equals(reader.readLine()))
			{
				ctd = new ContinuumToDiscretization();
				
				//读取特征名称个数
				int featureNameLen = Integer.valueOf(reader.readLine());
				String[] featureNameText = reader.readLine().split("\t");
				
				if(featureNameLen != featureNameText.length)
				{
					System.err.println("读取特征名称个数出错");
					System.exit(1);
				}
				//特征名称索引保存进featureNameIndex
		        Map<String, Integer> featureNameIndex = new HashMap<String, Integer>();
		        for (int i = 0; i < featureNameText.length; i++) {
		            String[] nameIndex = featureNameText[i].split(":");
		            if (nameIndex.length == 2) {
		            	featureNameIndex.put(nameIndex[0], Integer.valueOf(nameIndex[1]));
		            }
		        }
		        
		        //读取离散特征长度
		        int discretizationLen = Integer.valueOf(reader.readLine());
		        Double[][] discretizationMatrix = new Double[discretizationLen][];
		        //读取每列离散值
		        for (int i = 0; i < discretizationLen; i++) {
		        	int discretizationArrayLen = Integer.valueOf(reader.readLine());
		        	String[] discretizationArrayStr = reader.readLine().split("\t");
		        	if(discretizationArrayLen != discretizationArrayStr.length)
		        	{
		        		System.err.println("读取每列离散化值的个数出错");
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
				
				//离散结束标志
		        String discretizationFlag = reader.readLine();
				if (!discretizationFlag.equals("discretization end"))
				{
					System.err.println("读取离散值出错");
					System.exit(1);
				}
			}
			
			//读取结束标志
			String endFlag = reader.readLine();
			if (!endFlag.equals("end"))
			{
				System.err.println("读取模型文件出错");
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