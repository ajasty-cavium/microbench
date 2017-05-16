import java.io.IOException;
import java.io.FileOutputStream;

public class FileOutputStreamTest {
   public static void main(String[] args) throws IOException {
      
      FileOutputStream fos = null;
      int available = 0;
      int i=0;
        int bufsize=4096;
	int MB=10;
	int totalsize;

        if (args.length > 0) {
            try {
                bufsize = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Parameter " + args[0] + " must be an integer indicating size of the buffer to be used when writing.");
                System.exit(1);
            }
        }
        if (args.length > 1) {
            try {
                MB = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Parameter " + args[0] + " must be an integer indicating total bytes to write (in MB)");
                System.exit(1);
            }
        }
	totalsize = MB*1000*1000;

      byte[] bytes = new byte[bufsize];
	int nbytes;
      
      try{
         // create new stream
         fos = new FileOutputStream("testout.txt");
        Report.start(); 
         for (i=0; i<totalsize ; i+=bufsize)
         {
		fos.write(bytes);
         }
         // releases all system resources from the streams
         if(fos!=null)
         {
            fos.close();
         }
	Report.end();
	Report.report("FileOutputStream["+bufsize+"/"+MB+"MB]",MB);
      }catch(Exception ex){
         // if an I/O error occurs
         ex.printStackTrace();
      }finally{
         
      }
   }
}

