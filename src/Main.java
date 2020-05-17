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
	static private final int BLOCKS_IN_BUFFER = 1; // the number of blocks read/written at once
	static private RandomAccessFile randomAccessFile;
	static private int blockSize;
	static long reversePosition, position;
	
	public static void main(String[] args) throws IOException, URISyntaxException
	{
		long startTime = System.nanoTime();

		blockSize = (int) getPathFilesystem("/").getBlockSize();
		byte[] buffer1 = new byte[blockSize * BLOCKS_IN_BUFFER];
		byte[] buffer2 = new byte[blockSize * BLOCKS_IN_BUFFER];
		
		randomAccessFile = new RandomAccessFile(new File("files/input.txt"), "rw");
		
		position = 0;
		
		if(randomAccessFile.length() % blockSize != 0) //there is half full block in the end
		{
			reversePosition = randomAccessFile.length() - randomAccessFile.length() % blockSize;
			
//			System.out.println("_rev reading from: " + reversePosition);
			buffer1 = readFromPosition(reversePosition, buffer1);
			
//			System.out.println("_reading from: " + position + '\n');
			buffer2 = readFromPosition(position, buffer2);
			
			buffer1 = reverseArray(buffer1);
			buffer2 = reverseArray(buffer2);
			
			writeToPosition(position, buffer1);
			writeToPosition(reversePosition, buffer2);
			
			reversePosition -= blockSize;
			position += blockSize;
		}
		else
		{
			reversePosition = randomAccessFile.length() - blockSize;
		}
		
		
		while (reversePosition > position)
		{
//			System.out.println("rev reading from: " + reversePosition);
			buffer1 = readFromPosition(reversePosition, buffer1);
			
//			System.out.println("reading from: " + position);
			buffer2 = readFromPosition(position, buffer2);
			
			buffer1 = reverseArray(buffer1);
			buffer2 = reverseArray(buffer2);
			
			writeToPosition(position, buffer1);
			writeToPosition(reversePosition, buffer2);
			
			reversePosition -= blockSize;
			position += blockSize;
		}
		
		long endTime = System.nanoTime();
		System.out.println((endTime-startTime)/1000000);
//		if (reversePosition < 0 && reversePosition != -blockSize) //if something is left
//		{
//			System.out.println("rev reading from: " + reversePosition);
//
//			long negativeOffset = reversePosition;			
//			reversePosition = 0;
//			
//			buffer1 = readFromPosition(reversePosition, buffer1, blockSize + (int) negativeOffset);
//		}
	}
	
	private static byte[] readFromPosition(long position, byte[] buffer) throws IOException
	{
		return readFromPosition(position, buffer, buffer.length);
	}
	
	private static byte[] readFromPosition(long position, byte[] buffer, int length) throws IOException
	{
		buffer = new byte[blockSize * BLOCKS_IN_BUFFER]; //clear array
		
		randomAccessFile.seek(position);
		
		randomAccessFile.read(buffer, 0, length);
		
//		String myString = new String(buffer, Charset.forName("UTF-8"));
//		System.out.println(myString);
		
		return buffer;
	}
	
	private static void writeToPosition(long position, byte[] buffer) throws IOException
	{
		randomAccessFile.seek(position);
		
		randomAccessFile.write(buffer);
	}
	
	private static byte[] reverseArray(byte[] arr)
	{
		for (int left = 0, right = arr.length - 1; left < right; left++, right--) {
	        // swap the values at the left and right indices
	        byte temp = arr[left];
	        arr[left]  = arr[right];
	        arr[right] = temp;
	    }
		
		return arr;
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
