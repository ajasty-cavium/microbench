import java.io.IOException;
import java.io.FileInputStream;

public class LoopTest{
   public static void main(String[] args) throws IOException {
      
      FileInputStream fis = null;
      int available = 0;
      int i=0;
      
      try{
         // create new file input stream
         fis = new FileInputStream("test.txt");
         long start_time = System.nanoTime();
 
         // read till the end of the stream
         while((i=fis.read())!=-1)
         {
            // available bytes
  //          available = fis.available();
            
            // convert integer to character
//            char c = (char)i;
            
            // prints
 //           System.out.print("Available: "+available);
 //            System.out.println("; Read: "+c);
          }
         long end_time = System.nanoTime();
        double difference = (end_time - start_time)/1e6;
        System.out.println("IO time was " +difference);
        start_time = System.nanoTime();
        int j=0;
        for(int k=0;k<10000000;++k)
	{
          j=k;
	}
	 end_time = System.nanoTime();
        difference = (end_time - start_time)/1e6;
        System.out.println("Dummy loop time was " +difference);


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
