import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;


public class ArrayCopyTest{

   public static void main(String args[]) {
    
    int copyCount=10; 
    int copySize=100000000;

    int[] src = new int[copySize+1];
    for (int i = copySize; i >= 0; --i) {
        src[i] = i;
    }
    //return src;


 //   int[] src = newSrc(copySize + 1);
    int[] dst = new int[copySize + 1];
    Report.start();
    //long begin = System.nanoTime();
    for (int count = copyCount; count > 0; --count) {
        System.arraycopy(src, 1, dst, 0, copySize);
        dst[copySize] = src[copySize] + 1;
        System.arraycopy(dst, 0, src, 0, copySize);
        src[copySize] = dst[copySize];
    }
    //long end = System.nanoTime();
    Report.end();
    Report.report("Arraycopy","s");
    //System.out.println("Arraycopy: " + (end - begin) / 1e9 + " s");


 // aa.test(1000000,1000,10);
}
}
