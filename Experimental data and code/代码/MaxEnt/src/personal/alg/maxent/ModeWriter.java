package personal.alg.maxent;
/** 
 * @author Chang Shu
 * @data 2015年12月10日
 */

import java.io.*;
import java.util.Map;
import java.util.Set;

public class ModeWriter {

	/**
	 * 写入模型文件
	 * @param classifier
	 * @param modeFile
	 * @return
	 */
	public static boolean write(MaxentClassifier classifier,String modeFile)
	{
		boolean flag = false;
		//算法名称
		String algorithm = classifier.getAlgorithm();
		//权重
		double[] weight = classifier.weight;
		//特征信息
		DataProcess dp = classifier.dataProcess;
		Map<String,Integer> mapping = dp.mapping;
		Set<String> labels = dp.labels;
		int featureLen = dp.featureLen;
		int C = 0;
		//离散化信息
		ContinuumToDiscretization ctd = classifier.getContinuumToDiscretization();

		
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
					modeFile,false), "utf-8"), true);
			//算法名称
			pw.println(algorithm);
			//标签长度及内容
			pw.println(labels.size());
			for (String label : labels) {
				pw.print(label +"\t");
			}
			pw.println();
			pw.println("label end");
			//特征长度及内容
			if("gis".equals(algorithm))
			{
				//如果为gis算法则写入C
				pw.println(((GisDataProcess)dp).getC());
			}
			
			pw.println(featureLen);
			for (Map.Entry<String, Integer> entry : mapping.entrySet()) {
				pw.print(entry.getKey()+":"+entry.getValue()+"\t");
			}
			pw.println();
			pw.println("feature end");
			//权重长度及内容
			pw.println(weight.length);
			for (int i = 0; i < weight.length; i++) {
				pw.print(weight[i]+"\t");
			}
			pw.println();
			pw.println("weight end");
			
			//写入离散化数据
			if(ctd != null)
			{
				Map<String,Integer> featureNameIndex = ctd.getFeatureNameIndex();
				//写入开始标志
				pw.println("discretization start");
				//写入特征名称个数
				pw.println(featureNameIndex.size());
				//写入特征名称索引
				for (Map.Entry<String, Integer> entry : featureNameIndex.entrySet()) {
					pw.print(entry.getKey()+":"+entry.getValue()+"\t");
				}
				pw.println();
				
				Double[][] discretizationMatrix = ctd.getDiscretizationMatrix();
				//写入离散化特征的数量
				pw.println(discretizationMatrix.length);
				for (int i = 0; i < discretizationMatrix.length; i++) {
					//写入每列离散值个数
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