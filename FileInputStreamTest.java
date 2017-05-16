import java.io.IOException;
import java.io.FileInputStream;

public class FileInputStreamTest{
   public static void main(String[] args) throws IOException {
      
      FileInputStream fis = null;
      int available = 0;
      int i=0;
	    byte b=0;
	
	 byte[] byteBuffer;
	int bufsize=4096;

	if (args.length > 0) {
	    try {
        	bufsize = Integer.parseInt(args[0]);
    	    } catch (NumberFormatException e) {
        	System.err.println("Parameter " + args[0] + " must be an integer indicating size of the buffer to read.");
        	System.exit(1);
	    }
	}
	byteBuffer = new byte[bufsize];
      
      try{
         // create new file input stream
         fis = new FileInputStream("test.txt");
        Report.start(); 
         // read till the end of the stream
         while((i=fis.read(byteBuffer))!=-1)
         {
            // available bytes
            available = fis.available();
            int j;
	 	for (j=0; j<i; j++) {
		 b ^= byteBuffer[j];	
		}
         }
	Report.end();
	Report.report("FileInputStream["+bufsize+"]");
      }catch(Exception ex){
         // if an I/O error occurs
         ex.printStackTrace();
      }finally{
         System.out.println("b="+b);
         // releases all system resources from the streams
         if(fis!=null)
         {
            fis.close();
         }
      }
   }
}
