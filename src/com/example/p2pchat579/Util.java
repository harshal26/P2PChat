package com.example.p2pchat579;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class Util {
	
	public static int fromByteArray(byte[] bytes) {
	        return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
	   }

	static public byte[] arrayCopy(byte[]a,byte[]b)
    {
    	byte[] c = new byte[a.length + b.length];
    	System.arraycopy(a, 0, c, 0, a.length);
    	System.arraycopy(b, 0, c, a.length, b.length);
    	return c;
    }
	
	 static public byte[] intToBytes( final int i ) {
         ByteBuffer bb = ByteBuffer.allocate(4); 
         bb.putInt(i); 
         return bb.array();
     }
	 
	 public static boolean copyFile(byte[] buffer, int offset,int count,OutputStream out) {
	        	   
		 long startTime = System.currentTimeMillis();
	        try {	            
	        		
	        		out.write(buffer,offset,count);
	                long endTime=System.currentTimeMillis()-startTime;
	                Log.v("","Time taken to transfer all bytes is : "+endTime);
	            
	        } catch (IOException e) {
	            Log.d("Wrting File", e.toString());
	            return false;
	        }
	        return true;
	    }
	 




	 public static  String getRealPathFromURI(Uri contentURI,Context context) {
		 String result;
		 Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
		 if (cursor == null) { // Source is Dropbox or other similar local file path
			 result = contentURI.getPath();
		 } else { 
			 cursor.moveToFirst(); 
			 int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
			 result = cursor.getString(idx);
			 cursor.close();
		 }
		 return result;
	 }
	 
	 public static byte[] serialize(Object obj) throws IOException {
		    ByteArrayOutputStream out = new ByteArrayOutputStream();
		    ObjectOutputStream os = new ObjectOutputStream(out);
		    os.writeObject(obj);
		    return out.toByteArray();
		}
		public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
		    ByteArrayInputStream in = new ByteArrayInputStream(data);
		    ObjectInputStream is = new ObjectInputStream(in);
		    return is.readObject();
		}


}
