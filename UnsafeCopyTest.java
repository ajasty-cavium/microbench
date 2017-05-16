import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;
import sun.misc.Unsafe;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class UnsafeCopyTest{
    private static Unsafe unsafe;

    public static void prepareUnsafe() throws Exception {
        unsafe = makeInstance();
    }

    private static Unsafe makeInstance() throws Exception{
        Constructor<Unsafe> unsafeConstructor = Unsafe.class.getDeclaredConstructor();
        unsafeConstructor.setAccessible(true);
        unsafe = unsafeConstructor.newInstance();
        return unsafe;
    }


   public static void main(String args[]) {
	try {
    	  prepareUnsafe();
	} 
	catch(Exception e){
          e.printStackTrace();
	  return;
      	}
	
    long copyCount=10; 
    int copySize=100000000;

        if (args.length > 0) {
            try {
                copySize = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Parameter " + args[0] + " must be an integer indicating size of the buffer to be used when writing.");
                System.exit(1);
            }
        }
        if (args.length > 1) {
            try {
                copyCount = Long.parseLong(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Parameter " + args[0] + " must be an integer indicating total bytes to write (in MB)");
                System.exit(1);
            }
        }

	long memsrc=unsafe.allocateMemory(copySize+1);
	long memdst=unsafe.allocateMemory(copySize+1);
/*
    int[] src = new int[copySize+1];
    for (long i = copySize; i >= 0; --i) {
        src[i] = i;
    }


    int[] dst = new int[copySize + 1];
*/
    Report.start();
    for (long count = copyCount; count > 0; --count) {
	unsafe.copyMemory(memsrc+1,memdst,copySize);
	unsafe.copyMemory(memdst,memsrc,copySize);
/*
        unsafe.copyMemory(src, 1, dst, 0, copySize);
        dst[copySize] = src[copySize] + 1;
        unsafe.copyMemory(dst, 0, src, 0, copySize);
        src[copySize] = dst[copySize];
*/
    }
    Report.end();
    Report.report("UnsafeCopy,"+copySize+","+copyCount,2*(long)copySize*copyCount/1000000,"ms","MB/s");


}
}

