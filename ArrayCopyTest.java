import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;


public class ArrayCopyTest{

   public static void main(String args[]) {
    
    long copyCount=10; 
    int copySize=100000000;

        if (args.length > 0) {
            try {
                copySize = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Parameter " + args[0] + " must be an integer indicating size of copy.");
                System.exit(1);
            }
        }
        if (args.length > 1) {
            try {
                copyCount = Long.parseLong(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Parameter " + args[0] + " must be an integer indicating number of repeats");
                System.exit(1);
            }
        }

    int[] src = new int[copySize+1];
    for (int i = copySize; i >= 0; --i) {
        src[i] = i;
    }


    int[] dst = new int[copySize + 1];
    Report.start();
    for (long count = copyCount; count > 0; --count) {
        System.arraycopy(src, 1, dst, 0, copySize);
        dst[copySize] = src[copySize] + 1;
        System.arraycopy(dst, 0, src, 0, copySize);
        src[copySize] = dst[copySize];
    }
    Report.end();
    Report.report("Arraycopy,"+copySize+","+copyCount,2*(long)copySize*copyCount/1000000,"ms","MB/s");

   }
}

