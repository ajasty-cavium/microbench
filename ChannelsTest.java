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
        
        while(channel.read(byteBuffer) > 0){
            
            //limit is set to current position and position is set to zero
            byteBuffer.flip();
            
            while(byteBuffer.hasRemaining()){
                char ch = (char) byteBuffer.get();
              //  System.out.print(ch);
            }
        }
      }catch(IOException e){
          e.printStackTrace();
      }finally{
          in.close();
          channel.close();
      }
  }
}
 
