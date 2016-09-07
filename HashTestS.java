import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;


public class HashTestS {

    public static void main(String args[]) {

        HashMap<String, String> cache = new HashMap<String, String>();
		Report.start();

        for (int i=0;i<10000000;++i)
        {
            cache.put(String.valueOf(i),String.valueOf(i+100));
        }
		Report.end();
		Report.report("HashMap<S,S>-put");


        String k="";
		Report.start();
        for (int i=0;i<10000000;++i)
        {
            k=cache.get(String.valueOf(i));
        }
		Report.end();
		Report.report("HashMap<S,S>-remove");

    }

}
