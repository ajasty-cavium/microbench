import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;


public class ArrayCopyTest{

   public static void main(String args[]) {

 



int copySize=10000000;
 int copyCount=100;
 int testRep=10;
    System.out.println("Copy size = " + copySize);
    System.out.println("Copy count = " + copyCount);
    System.out.println();
    for (int i = testRep; i > 0; --i) {
        copy(copySize, copyCount);
        loop(copySize, copyCount);
    }
    System.out.println();
}

public static void copy(int copySize, int copyCount) {
    int[] src = newSrc(copySize + 1);
    int[] dst = new int[copySize + 1];
    long begin = System.nanoTime();
    for (int count = copyCount; count > 0; --count) {
        System.arraycopy(src, 1, dst, 0, copySize);
        dst[copySize] = src[copySize] + 1;
        System.arraycopy(dst, 0, src, 0, copySize);
        src[copySize] = dst[copySize];
    }
    long end = System.nanoTime();
    System.out.println("Arraycopy: " + (end - begin) / 1e9 + " s");
}

public static void loop(int copySize, int copyCount) {
    int[] src = newSrc(copySize + 1);
    int[] dst = new int[copySize + 1];
    long begin = System.nanoTime();
    for (int count = copyCount; count > 0; --count) {
        for (int i = copySize - 1; i >= 0; --i) {
            dst[i] = src[i + 1];
        }
        dst[copySize] = src[copySize] + 1;
        for (int i = copySize - 1; i >= 0; --i) {
            src[i] = dst[i];
        }
        src[copySize] = dst[copySize];
    }
    long end = System.nanoTime();
    System.out.println("Man. loop: " + (end - begin) / 1e9 + " s");
}

public static  int[] newSrc(int arraySize) {
    int[] src = new int[arraySize];
    for (int i = arraySize - 1; i >= 0; --i) {
        src[i] = i;
    }
    return src;
}
}
