package personal.alg.maxent;

/** 
* @author Chang Shu
* @data 2015��11��11��
* ���б��ж�ȡ����������Ϊ�˱�������ı���ȡ�ķ�ʽһ�£���������������
*/
import java.util.Iterator;
import java.util.List;

public class ListByLineDataStreamS implements DataStream {
	Iterator<String> iterator;
    String next;
    
    public ListByLineDataStreamS (List<String> featureStrAll) {
    	iterator = featureStrAll.iterator();
    }
    
    
    public Object nextToken () 
	{
		String next = null;
		
		if (iterator.hasNext())
			next = (String)iterator.next();
	
		return next;
    }

    public boolean hasNext () {
	    return iterator.hasNext();
    }
 
}
