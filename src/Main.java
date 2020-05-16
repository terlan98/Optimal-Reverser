import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main
{
	static private final int BLOCKS_IN_BUFFER = 1;
	static private RandomAccessFile randomAccessFile;
	static private int blockSize;
	static long position, reversePosition;
	
	public static void main(String[] args) throws IOException, URISyntaxException
	{
		blockSize = (int) getPathFilesystem("/").getBlockSize();
		byte[] buffer = new byte[blockSize * BLOCKS_IN_BUFFER];
		
		randomAccessFile = new RandomAccessFile(new File("files/input.txt"), "rw");
		
		position = randomAccessFile.length() - blockSize;
		
		while (position > 0)
		{
			readFromPosition(position, buffer);
			
			position -= blockSize;
		}
				
		if (position < 0) //if something is left
		{
			long negativeOffset = position;			
			position = 0;
			
			readFromPosition(position, buffer, blockSize + (int) negativeOffset);
			
		}
	}
	
	public static void readFromPosition(long position, byte[] buffer) throws IOException
	{
		readFromPosition(position, buffer, buffer.length);
	}
	
	public static void readFromPosition(long position, byte[] buffer, int length) throws IOException
	{
		buffer = new byte[blockSize * BLOCKS_IN_BUFFER]; //clear array
		
		randomAccessFile.seek(position);
		
		randomAccessFile.read(buffer, 0, length);

		randomAccessFile.seek(position);
		
		randomAccessFile.writeBytes("1111");
		
		String myString = new String(buffer, Charset.forName("UTF-8"));
		System.out.println(myString);
	}
	
	public static FileStore getPathFilesystem(String path) throws URISyntaxException, IOException
	{
		URI rootURI = new URI("file:///");
		Path rootPath = Paths.get(rootURI);
		Path dirPath = rootPath.resolve(path);
		FileStore dirFileStore = Files.getFileStore(dirPath);
		
		return dirFileStore;
	}
}
