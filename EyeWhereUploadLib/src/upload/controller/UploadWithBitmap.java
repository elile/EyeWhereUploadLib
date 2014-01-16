package upload.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import upload.constant.values;
import upload.dal.BitmapImagePost;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.telephony.TelephonyManager;
import android.util.Log;

public class UploadWithBitmap 
{

	private Context c;
	private static int counter=0;
	private static double sumTime=0;
	ArrayList<Bitmap> bmps ;
	Bitmap b;
	String ret = "";

	public int getCounter() {
		return counter;
	}

	public double getSumTime() {
		return sumTime;
	}

	public UploadWithBitmap(Context c) 
	{
		this.c = c;
		this.counter = 0;
	}

	public String uploadBitmapWithCrop(Bitmap bmp) 
	{
//		Log.e("eli", "big Bitmap size before compress = " + (sizeOf(bmp)/1048576) + "MB 1.67MB real");
		
		bmps = splitImage(bmp, 4);
		
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
			try {
				t.join(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
			if (QualityGetSet.getQuality(c) == -1) 
			{
				QualityGetSet.setQuality(c,90);
			}
			QualityGetSet.setQuality(c,30);

//			Log.e("eli", "Bitmap size before compress = " + (sizeOf(bmp)/1048576) + "MB");

			long start = System.currentTimeMillis();

			ByteArrayOutputStream bos_output = new ByteArrayOutputStream();
			boolean isCompress = bmp.compress(CompressFormat.JPEG , QualityGetSet.getQuality(c), bos_output);
			byte[] bos = bos_output.toByteArray();
//			Log.e("eli", "before zip "+bos.toByteArray().toString()+"");

//			ByteArrayInputStream fileInputStream = new ByteArrayInputStream(bos);

//			Log.e("eli", "isCompress = " + isCompress);	
			long end=System.currentTimeMillis()-start; 
			double seconds = (double)end / 1000.0;
			sumTime+=seconds;
//			Log.e("eli end compression", seconds+"");
//			Log.e("eli", "byte array size = " + (bos.length/1024) +" KB");
			
//			for (int i = 1; i < bos.length; i++) {
//				if(bos[i]<0)
//					bos[i]=bos[i-1];
//				Log.e("eli", bos[i]+"");
//
//			}

			return new BitmapImagePost().postBMP(null, values.URL_UPLOAD_ONE_IMAGE, getSN()+"_"+counter+"_"+id, bos.length+102,bos);
		}
	}

	
	private String getSN()
	{
		TelephonyManager tManager = (TelephonyManager)c.getSystemService(Context.TELEPHONY_SERVICE);
		String uid = tManager.getDeviceId();
		return uid;
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
