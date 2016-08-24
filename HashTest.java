import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;


public class HashTest{

   public static void main(String args[]) {

	HashMap<Integer, Integer> cache = new HashMap<Integer, Integer>();
	 long start_time2 = System.nanoTime();	

	for(int i=0;i<10000000;++i)
		{
		cache.put(i,i+100);
		}	
        long end_time2 = System.nanoTime();
        double difference2 = (end_time2 - start_time2)/1e6;        
        System.out.println("time was " +difference2);

	
	int k=0;
	long start_time = System.nanoTime();
	for(int i=0;i<10000000;++i)
                {
                k=cache.remove(i);
                }

	long end_time = System.nanoTime();
	double difference = (end_time - start_time)/1e6;
        System.out.println("time was " +difference);

       
}
	
}
