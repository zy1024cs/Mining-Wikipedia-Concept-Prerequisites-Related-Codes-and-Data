package personal.alg.test;


import personal.alg.maxent.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

public class CreateModeNew {

	public CreateModeNew() {
		// TODO Auto-generated constructor stub
	}
	
	
	public static void train(String trainFileName,String modelFileNameNew)
	{
	    FileReader datafr = null;
		try {
			datafr = new FileReader(new File(trainFileName));
			FeatureDataStream es = new FeatureDataStream(new PlainTextByLineDataStreamS(datafr));
			//ѵ������
			MaxentClassifier m = MaxentTrainer.train(es, "iis", 1000);
			//����ģ��
			boolean flag = ModeWriter.write(m, modelFileNameNew);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
	}
	
	public static double evalNew(MaxentClassifier classifier,String feature,int pro)
	{
		double prob = 0.0 ;
		FeatureData data = Util.createFeatureData(feature);

		ContinuumToDiscretization ctd = classifier.getContinuumToDiscretization();
		//�����ѵ����ģ��������������,��Ԥ�����ݻ�������ɢ����
		if (ctd != null){
			ctd.dataDiscretization(data,ctd.getDiscretizationMatrix(),ctd.getFeatureNameIndex());
		}

		DictionaryProbDist pDist = classifier.probClassify(data);
		/**Ԥ��Ϊ1�ĸ��ʣ����Ϊѵ�����ݵĸ������
		 * ����:ѵ������ÿ�������һ����ʶΪ"2",��ôԤ��Ϊ"2"�ĸ��ʾ�дprob = pDist.prob("2")*/
//		prob = pDist.prob("1");
//		prob = pDist.prob("2");
//		prob = pDist.prob("3");
//		prob = pDist.prob("4");
//		prob = pDist.prob("5");
//		prob = pDist.prob(pro + "");
//		System.out.println(pDist.getProb());
		Map<String,Double> map = pDist.getProb();
//		pDist.getProb();
		
		// ��Ϊ�������
//		double A = pDist.prob("1");
//		double B = pDist.prob("2");
//		double C = pDist.prob("3");
//		double D = pDist.prob("4");
//		double E = pDist.prob("5");
//		System.out.println(A + " " + B + " " + C + " " + D + " " + E);
//		if (A >= B && A >= C && A >= D && A >= E) {
//			return 1.0;
//		}
//		else if (B >= A && B >= C && B >= D && B >= E) {
//			return 2.0;
//		}
//		else if (C >= A && C >= B && C >= D && C >= E) {
//			return 3.0;
//		}
//		else if (D >= A && D >= B && D >= C && D >= E) {
//			return 4.0;
//		}
//		else {
//			return 5.0;
//		}
		
		double A = pDist.prob("1");
		double B = pDist.prob("2");
		double C = pDist.prob("3") + pDist.prob("4") + pDist.prob("5");
		System.out.println(A + " " + B + " " + C);
		if (A >= B && A >= C) {
			return 1;
		}
		else if (B >= A && B >= C) {
			return 2;
		}
		else {
			return 3;
		}
		
		
	}
	
	public static int right_of_num = 0;
	public static int all_of_num = 0;
	
	public static void predict(String testFileName,String modelFileNameNew,int pro)
	{
		MaxentClassifier classifier = null;
		DataStream ds = null;
		try {
			//��ȡģ��
			classifier = ModeReader.read(modelFileNameNew);
			ds = new PlainTextByLineDataStreamS(new FileReader(new File(testFileName)));
			int sum = 0;
			int right = 0;
			while (ds.hasNext()) {
			    String s = (String)ds.nextToken();
//			    double p = evalNew(classifier,s,pro);
			    double p = evalNew(classifier,s,pro);
			    //ʵ�����������׼ȷ��
			    String type = s.substring(s.lastIndexOf(' ') + 1);
//			    System.out.println(type);
			    sum += 1;
			    
			    
			    //������
//			    if ((Integer.parseInt(type) == (int)p) == true) {
//			    	right += 1;
//			    }
			    
			    
			    
			    int i, j = 0;
			    i = Integer.parseInt(type);
			    j = (int)p;
			    
			    //�������
//			    if (Integer.parseInt(type) >= 3) {
//			    	if ((int)p == 3) {
//			    		right += 1;
//			    	}
//			    	i = 3;
//			    }
//			    else {
//			    	if (Integer.parseInt(type) == (int)p) {
//			    		right += 1;
//			    	}
//			    }
			    
			    //�������
			    if (Integer.parseInt(type) == (int)p) {
		    		right += 1;
		    	}
			    
			    
			    
			    
			    
			    //������ݵĸ���
//			    System.out.println(s+"---------"+p);
			    // p ΪԤ��ֵ
			    System.out.println((int)p + " " + type);
			    
			    
			    matrix[i-1][j-1] += 1;
			    
			}
			System.out.println(right * 1.0 / sum * 100);
			System.out.println(right);
			right_of_num += right;
			all_of_num += sum;
		}
		catch (Exception e) {
		      e.printStackTrace();
		    }
	}
	
	public static int[][] matrix = new int[3][3];
	
	
	public static void main(String[] args) {
		for (int i=0;i<3;i++) {
			for (int j=0;j<3;j++) {
				matrix[i][j] = 0;
			}
		}
		String modelFileNameNew = "new_mode.txt";
		String trainFileName = "train_data.txt";
		String testFileName =  "test_data.txt";
		String title = "temp/";
		for (int file = 1;file <= 10;file ++) {
			//ѵ������
			train(title + file + "/" + trainFileName, modelFileNameNew);
			//Ԥ����
			predict(title + file + "/" + testFileName, modelFileNameNew, 1);
		}
		System.out.println("------------");
		System.out.println(right_of_num);
		System.out.println(all_of_num);
		System.out.println((right_of_num * 1.0 / (all_of_num * 1.0) * 100) + "%");
	
	
		for (int i=0;i<3;i++) {
			for (int j=0;j<3;j++) {
				System.out.print(matrix[i][j] + " ");
			}
			System.out.println();
		}
	
	
	}
}
