package personal.alg.maxent;
/** 
 * @author Jason Baldridge
 * @data 2010年9月16日
 * opennlp里面的一个类，借来用下
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class PlainTextByLineDataStreamS  implements DataStream {
    BufferedReader dataReader;
    String next;
    
    public PlainTextByLineDataStreamS (Reader dataSource) {
	dataReader = new BufferedReader(dataSource);
	try {
	    next = dataReader.readLine();
	}
	catch (IOException e) {
	    e.printStackTrace();
	}
    }
    
    public Object nextToken () {
	String current = next;
	try {
	    next = dataReader.readLine();
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
	return current;
    }

    public boolean hasNext () {
	return next != null;
    }
 
}
