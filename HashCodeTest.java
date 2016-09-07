import java.util.*;

public class HashCodeTest
{
    static int ITS = 10000000;
    static Object O = new Object();

    public static void main(String[] args)
    {
        TreeSet<Integer> large = new TreeSet<Integer>();
        Random ran = new Random();
        for (int i=0; i<1000000; i++) {
            large.add(ran.nextInt());
        }

        testIdentityHashCode(large);
		Report.report("System.identityHashCode#1",ITS,"ns");
        testHashCode(large);
		Report.report("Object.hashCode#1",ITS,"ns");
        testIdentityHashCode(large);
		Report.report("System.identityHashCode#2",ITS,"ns");
        testHashCode(large);
		Report.report("Object.hashCode#2",ITS,"ns");
    }

    protected static void testIdentityHashCode(TreeSet<Integer> large)
    {
		Report.start();
        for (int i=0; i<ITS; i++) {
            Object o = new Container(large);
            System.identityHashCode(o);
        }
		Report.end();
    }

    protected static void testHashCode(TreeSet<Integer> large)
    {
		Report.start();
        for (int i=0; i<ITS; i++) {
            Object o = new Container(large);
            o.hashCode();
        }
		Report.end();
    }

    private static class Container {
        private final Object o;

        public Container(Object o)
        {
            this.o = o;
        }
    }
}
