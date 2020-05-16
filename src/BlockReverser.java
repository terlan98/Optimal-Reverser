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

public class BlockReverser
{
	static private final int BLOCKS_IN_BUFFER = 1;
	
	private RandomAccessFile randomAccessFile;
	private long position, reversePosition;
	private int blockSize;
	
	public BlockReverser() throws IOException, URISyntaxException 
	{
		blockSize = (int) getPathFilesystem("/").getBlockSize();		
	}
	
	public void reverse(String filePath) throws IOException
	{
		byte[] buffer = new byte[blockSize * BLOCKS_IN_BUFFER];

		randomAccessFile = new RandomAccessFile(new File(filePath), "rw");
		
		reversePosition = randomAccessFile.length() - blockSize;
		position = 0;
		
		
	}
	
	public void readFromPosition(long position, byte[] buffer) throws IOException
	{
		readFromPosition(position, buffer, buffer.length);
	}
	
	public void readFromPosition(long position, byte[] buffer, int length) throws IOException
	{
		buffer = new byte[blockSize * BLOCKS_IN_BUFFER]; //clear array
		
		randomAccessFile.seek(position);
		
		randomAccessFile.read(buffer, 0, length);
		
		randomAccessFile.seek(position);
		
		randomAccessFile.writeBytes("1111");
		
		String myString = new String(buffer, Charset.forName("UTF-8"));
		System.out.println(myString);
	}
	
	public FileStore getPathFilesystem(String path) throws URISyntaxException, IOException
	{
		URI rootURI = new URI("file:///");
		Path rootPath = Paths.get(rootURI);
		Path dirPath = rootPath.resolve(path);
		FileStore dirFileStore = Files.getFileStore(dirPath);
		
		return dirFileStore;
	}
}
