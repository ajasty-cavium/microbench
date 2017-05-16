import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class ChannelsTest {
  public static void main(String[] args) throws IOException{
      InputStream in = null;
      ReadableByteChannel channel = null;
        int bufsize=4096;

        if (args.length > 0) {
            try {
                bufsize = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Parameter " + args[0] + " must be an integer indicating size of the buffer to read.");
                System.exit(1);
            }
        }

      try{
        //opens a file to read from the given location
        in = new FileInputStream("test.txt");
        
        //returns ReadableByteChannel instance to read the file
        channel = Channels.newChannel(in);   
        
        //allocate byte buffer size
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bufsize);
		long buffers=0,bytes=0;
		Report.start();
	int nread,nfree;
        
        while((nread=channel.read(byteBuffer)) > 0){
            
            //limit is set to current position and position is set to zero
            byteBuffer.flip();
	    buffers++;
            
            while(byteBuffer.hasRemaining()){
            	char ch = (char) byteBuffer.get();
		bytes++;
            }
	    byteBuffer.clear();
        }
		Report.end();
		Report.report("ChannelTest["+bufsize+"]",buffers);
      }catch(IOException e){
          e.printStackTrace();
      }catch(Exception e){
          e.printStackTrace();
      }finally{
          in.close();
          channel.close();
      }
  }
}
 
