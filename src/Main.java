import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This program reverses the content of a given file.
 * The reversal is performed in place, without writing to any new file. <br> <br>
 * The input file should be located in the same directory as the source code. 
 * The default name for the file is "input.txt" but you can change the FILE_NAME variable in code. 
 * The program is not limited to txt files; it can reverse any file.
 * @author Tarlan Ismayilsoy
 * @hidden asd
 */
public class Main
{
	static private final String FILE_NAME = "rev.txt"; // File name
	static private final int BLOCKS_IN_BUFFER = 200; // The number of blocks read/written at once. Feel free to adjust
	
	static private RandomAccessFile randomAccessFile;
	static private int blockSize;
	static private long rightPosition, leftPosition;
	
	public static void main(String[] args) throws IOException, URISyntaxException
	{
		long startTime = System.nanoTime();
		
		//Get the block size of the file system
		blockSize = (int) getPathFilesystem(System.getProperty("user.dir")).getBlockSize();
		
		byte[] buffer1 = new byte[blockSize * BLOCKS_IN_BUFFER];
		byte[] buffer2 = new byte[blockSize * BLOCKS_IN_BUFFER];
		
		//Open the file with RW permissions
		randomAccessFile = new RandomAccessFile(new File("files/" + FILE_NAME), "rw");
		
		leftPosition = 0;
		rightPosition = randomAccessFile.length() - blockSize * BLOCKS_IN_BUFFER;
		
		if(rightPosition <= 0) //if the file is smaller than the buffer size, handle it at once
		{
			rightPosition = 0;
			
			buffer1 = readFromPosition(rightPosition, (int) randomAccessFile.length());
			buffer1 = reverseArray(buffer1);
			writeToPosition(rightPosition, buffer1, (int) randomAccessFile.length());
		}
		else // file needs to be handled in blocks
		{
			while (rightPosition >= leftPosition)
			{
				buffer1 = readFromPosition(rightPosition, buffer1.length);
				buffer2 = readFromPosition(leftPosition, buffer2.length);
				
				buffer1 = reverseArray(buffer1);
				buffer2 = reverseArray(buffer2);
				
				writeToPosition(leftPosition, buffer1);
				writeToPosition(rightPosition, buffer2);
				
				rightPosition -= blockSize * BLOCKS_IN_BUFFER;
				leftPosition += blockSize * BLOCKS_IN_BUFFER;
			}
		}
		
		randomAccessFile.close();
		
		long endTime = System.nanoTime();
		System.out.println("Execution time: " + (endTime - startTime) / 1000000 + " ms");
	}
	
	/**
	 * Reads <i>length</i> bytes from the given position into the given byte array
	 * @param position Position to start reading from
	 * @param length Number of bytes to read
	 * @return The byte array containing the read result
	 * @throws IOException
	 */
	private static byte[] readFromPosition(long position, int length) throws IOException
	{
		byte[] buffer = new byte[length]; //clear array
		
		randomAccessFile.seek(position);
		randomAccessFile.read(buffer, 0, length);
		
		return buffer;
	}
	
	/**
	 * Writes the given byte array to the given position in file.
	 * @param position Position to start writing from
	 * @param buffer Byte array to write
	 * @throws IOException If an I/O error occurs
	 */
	private static void writeToPosition(long position, byte[] buffer) throws IOException
	{
		writeToPosition(position, buffer, buffer.length);
	}
	
	/**
	 * Writes <i>length</i> bytes from the given byte array to the given position in file.
	 * @param position Position to start writing from
	 * @param buffer Byte array to write
	 * @param length Number of bytes to write
	 * @throws IOException If an I/O error occurs
	 */
	private static void writeToPosition(long position, byte[] buffer, int length) throws IOException
	{
		randomAccessFile.seek(position);
		
		randomAccessFile.write(buffer, 0, length);
	}
	
	/**
	 * Reverses the given array.
	 * @param arr
	 * @return Reversed version of the array
	 */
	private static byte[] reverseArray(byte[] arr)
	{
		for (int left = 0, right = arr.length - 1; left < right; left++, right--)
		{
			// swap the values at the left and right indices
			byte temp = arr[left];
			arr[left] = arr[right];
			arr[right] = temp;
		}
		
		return arr;
	}
	
	/**
	 * Returns the file system that the program resides in.
	 * @param path
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public static FileStore getPathFilesystem(String path) throws URISyntaxException, IOException
	{
		URI rootURI = new URI("file:///");
		Path rootPath = Paths.get(rootURI);
		Path dirPath = rootPath.resolve(path);
		FileStore dirFileStore = Files.getFileStore(dirPath);
		
		return dirFileStore;
	}
}
