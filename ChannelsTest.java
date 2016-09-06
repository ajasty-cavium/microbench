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
      try{
        //opens a file to read from the given location
        in = new FileInputStream("test.txt");
        
        //returns ReadableByteChannel instance to read the file
        channel = Channels.newChannel(in);   
        
        //allocate byte buffer size
        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
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
              //  System.out.print(ch);
            }
	    byteBuffer.clear();
        }
		Report.end();
		Report.report("ChannelTest",buffers);
      }catch(IOException e){
          e.printStackTrace();
      }catch(Exception e){
          e.printStackTrace();
      }finally{
	System.out.println("Finally");
          in.close();
          channel.close();
      }
  }
}
 
