package personal.alg.maxent;

/** 
* @author Chang Shu
* @data 2015年11月11日
* 从列表中读取内容特征，为了保持与从文本读取的方式一致，所以添加了这个类
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
