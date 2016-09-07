import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;


public class HashTest {

    public static void main(String args[]) {

        HashMap<Integer, Integer> cache = new HashMap<Integer, Integer>();
		
		Report.start();
        for (int i=0;i<10000000;++i)
        {
            cache.put(i,i+100);
        }
		Report.end();
		Report.report("HashMap<I/I>-put");


        int k=0;
		Report.start();
        for (int i=0;i<10000000;++i)
        {
            k=cache.remove(i);
        }
		Report.end();
		Report.report("HashMap<I/I>-remove");


    }

}
