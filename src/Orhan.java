import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.Instant;

public class Orhan
{
	
	static final String FILEPATH = "files/input.txt";
	
	public static void main(String[] args) throws IOException
	{
		File file = new File(FILEPATH);
		long fileSize = file.length();
		
		if (fileSize <= 4096)
		{
			System.out.println("It is small file");
			Instant starts = Instant.now();
			byte[] temp = new byte[1];
			for (long i = 0; i < (fileSize / 2); i++)
			{
				temp = readFromFile(FILEPATH, i, 1);
				writeToFile(FILEPATH, readFromFile(FILEPATH, fileSize - i - 1, 1), i);
				writeToFile(FILEPATH, temp, fileSize - i - 1);
			}
			Instant ends = Instant.now();
			System.out.println("It took " + Duration.between(starts, ends).toSeconds() + " seconds to reverse");
		}
		else
		{
			System.out.println("It is big file");
			
			byte[] buffer1 = new byte[4096];
			byte[] buffer2 = new byte[4096];
			byte[] tempBuf = new byte[4096];
			
			Instant starts = Instant.now();
			
			long k = fileSize - 4096;
			for (long i = 0; i <= k; i = i + 4096)
			{
				System.out.println(i + " , " + k);
				buffer1 = readFromFile(FILEPATH, i, 4096);
				buffer2 = readFromFile(FILEPATH, k - 1, 4096);
				
				for (int j = 0; j < 4096; j++)
				{
					tempBuf[j] = buffer1[j];
					buffer1[j] = buffer1[4096 - j - 1];
					buffer1[4096 - j - 1] = tempBuf[j];
				}
				
				for (int j = 0; j < 4096; j++)
				{
					tempBuf[j] = buffer2[j];
					buffer2[j] = buffer2[4096 - j - 1];
					buffer2[4096 - j - 1] = tempBuf[j];
				}
				
				writeToFile(FILEPATH, buffer2, i);
				writeToFile(FILEPATH, buffer1, k - 1);
				
				k = k - 4096;
				
			}
			Instant ends = Instant.now();
			System.out.println("It took " + Duration.between(starts, ends).toSeconds() + " seconds to reverse");
			
		}
		
	}
	
	private static byte[] readFromFile(String filePath, long position, long size) throws IOException
	{
		RandomAccessFile file = new RandomAccessFile(filePath, "r");
		file.seek(position);
		byte[] bytes = new byte[(int) size];
		
		file.read(bytes);
		
		String myString = new String(bytes, Charset.forName("UTF-8"));
		System.out.println(myString);
		
		file.close();
		return bytes;
	}
	
	private static void writeToFile(String filePath, byte[] data, long position) throws IOException
	{
		RandomAccessFile file = new RandomAccessFile(filePath, "rw");
		file.seek(position);
		file.write(data);
		file.close();
	}
	
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
	
}
