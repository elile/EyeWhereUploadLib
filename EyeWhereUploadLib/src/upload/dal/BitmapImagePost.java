package upload.dal;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import android.util.Log;

public class BitmapImagePost
{

	public String postBMP(InputStream fileInputStream, String serverPath, String fileNameonserver) 
	{
		String fileName = fileNameonserver+".jpg";
		HttpURLConnection conn = null;
		DataOutputStream dos = null;  
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		try {
			URL url = new URL(serverPath);
			// Open a HTTP  connection to  the URL
			conn = (HttpURLConnection) url.openConnection(); 
			conn.setDoInput(true); // Allow Inputs
			conn.setDoOutput(true); // Allow Outputs
			conn.setUseCaches(false); // Don't use a Cached Copy
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("ENCTYPE", "multipart/form-data");
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			conn.setRequestProperty("uploaded_file", fileName); 
			dos = new DataOutputStream(conn.getOutputStream());
			dos.writeBytes(twoHyphens + boundary + lineEnd); 
			dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""	+ fileName + "\"" + lineEnd);
			dos.writeBytes(lineEnd);
			// create a buffer of  maximum size
			bytesAvailable = fileInputStream.available(); 
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];
			// read file and write it into form
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
			while (bytesRead > 0) 
			{
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);   
			}
			// send multipart form data necesssary after file data
			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
			// Responses from the server (code and message)
			Log.i("uploadFile", "HTTP Response: " + conn.getResponseMessage() + ": " + conn.getResponseCode());
			if(conn.getResponseCode() == 200)
			{
				Log.i("uploadFile", "upload File success"); 
			}    
			//close the streams
			fileInputStream.close();
			dos.flush();
			dos.close();
			String response= "";
			//start listening to the stream
			Scanner inStream = new Scanner(conn.getInputStream());
			//process the stream and store it in StringBuilder
			while(inStream.hasNextLine())
				response+=(inStream.nextLine());
			return response;
		} 
		catch (MalformedURLException ex) 
		{
			ex.printStackTrace();
			Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
			return "";
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			Log.e("Upload file to server Exception", "Exception : "  + e.getMessage(), e); 
			return "";
		}
	}

}
