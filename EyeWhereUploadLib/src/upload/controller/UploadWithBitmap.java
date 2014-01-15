package upload.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.GZIPOutputStream;

import upload.constant.values;
import upload.dal.BitmapImagePost;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

public class UploadWithBitmap 
{

	private Activity a;
	private int counter=0;
	ArrayList<Bitmap> bmps ;
	Bitmap b;
	String ret = "";


	public UploadWithBitmap(Activity a) 
	{
		this.a = a;
		this.counter = 0;
	}

	public String uploadBitMapWithCrop(Bitmap bmp) 
	{
//		Log.e("eli", "big Bitmap size before compress = " + (sizeOf(bmp)/1048576) + "MB 1.67MB real");
		
		bmps = splitImage(bmp, 20);
		
		for (Bitmap bitmap : bmps) 
		{
			b=bitmap;
			final Thread t = new Thread(){
				@Override
				public void run() {
					long start = System.nanoTime();

					ret += uploadBitmap(b, getId()+"") ;

					long end=System.nanoTime()-start; 
					double seconds = (double)end / 1000000000.0;
					Log.e("eli", getId()+" "+seconds);
				}
				
			};t.start();
			
		}
		return ret;
	}

	public String uploadBitmap(Bitmap bmp, String id) 
	{
		if (bmp == null) 
		{
			Log.e("eli", "Bitmap = null");
			return "";
		}
		else 
		{
			counter++;
			if (QualityGetSet.getQuality(a) == -1) 
			{
				QualityGetSet.setQuality(a,90);
			}
			QualityGetSet.setQuality(a,50);

//			Log.e("eli", "Bitmap size before compress = " + (sizeOf(bmp)/1048576) + "MB");

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bmp.compress(CompressFormat.JPEG , QualityGetSet.getQuality(a), bos);
			
//			Log.e("eli", "before zip "+bos.toByteArray().toString()+"");

			ByteArrayInputStream fileInputStream = new ByteArrayInputStream( bos.toByteArray());

//			Log.e("eli", "Bitmap size after compress = " + (sizeOf(bmp)/1048576) + "MB");

			return new BitmapImagePost().postBMP(fileInputStream, values.URL_UPLOAD_ONE_IMAGE, getSN()+"_"+counter+"_"+id);
		}
	}

	private byte[] compressByteArray(byte[] byteArray) 
	{
		try {
			byte[] dataToCompress = byteArray;
			ByteArrayOutputStream byteStream =new ByteArrayOutputStream(dataToCompress.length);
			GZIPOutputStream zipStream = new GZIPOutputStream(byteStream);
			zipStream.write(dataToCompress);
			zipStream.close();
			byteStream.close();
			return byteStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return byteArray;

	}

	private String getSN()
	{
		TelephonyManager tManager = (TelephonyManager)a.getSystemService(Context.TELEPHONY_SERVICE);
		String uid = tManager.getDeviceId();
		return uid;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	private int sizeOf(Bitmap data) 
	{
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) 
		{
			return data.getRowBytes() * data.getHeight();
		} else {
			return data.getByteCount();
		}
	}

	private ArrayList<Bitmap> splitImage(Bitmap bmp, int smallimage_Numbers)
	{      
		//For the number of rows and columns of the grid to be displayed
		int rows,cols;
		//For height and width of the small image smallimage_s
		int smallimage_Height,smallimage_Width;
		//To store all the small image smallimage_s in bitmap format in this list
		ArrayList<Bitmap> smallimages = new ArrayList<Bitmap>(smallimage_Numbers);
		Bitmap bitmap = bmp;
		Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
		rows = cols = (int) Math.sqrt(smallimage_Numbers);
		smallimage_Height = bitmap.getHeight()/rows;
		smallimage_Width = bitmap.getWidth()/cols;
		//xCo and yCo are the pixel positions of the image smallimage_s
		int yCo = 0;
		for(int x=0; x<rows; x++){
			int xCo = 0;
			for(int y=0; y<cols; y++){
				smallimages.add(Bitmap.createBitmap(scaledBitmap, xCo, yCo, smallimage_Width, smallimage_Height));
				xCo += smallimage_Width;
			}
			yCo+= smallimage_Height;
		}
		return smallimages;
	}
}
