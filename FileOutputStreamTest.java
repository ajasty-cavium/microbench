import java.io.IOException;
import java.io.FileOutputStream;

public class FileOutputStreamTest {
   public static void main(String[] args) throws IOException {
      
      FileOutputStream fis = null;
      int available = 0;
      int i=0;
      
      try{
         // create new file input stream
         fis = new FileOutputStream("testout.txt");
        Report.start(); 
         // read till the end of the stream
         for (i=0; i<(10*1024*1024) ; i++)
         {
            // available bytes
		fis.write(42);
            
            // convert integer to character
 //           char c = (char)i;
            
            // prints
 //           System.out.print("Available: "+available);
 //            System.out.println("; Read: "+c);
         }
	Report.end();
	Report.report("FileOutputStream");
      }catch(Exception ex){
         // if an I/O error occurs
         ex.printStackTrace();
      }finally{
         
         // releases all system resources from the streams
         if(fis!=null)
         {
            fis.close();
         }
      }
   }
}

