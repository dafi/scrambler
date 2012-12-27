package com.dafi.scambler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

public class ScramblerUtils {
	public static int[] arrayRange(int count) {
		int[] arr = new int[count];
		
		for (int i = 0; i < count; i++) {
			arr[i] = i;
		}
		return arr;
	}

	public static int[] shuffle(int[] arr) {
	    for (int i = arr.length - 1; i >= 0; --i) {
	        // swap indexes
	        int r = (int)Math.rint(Math.random() * i);
	        int t = arr[i];
	        arr[i] = arr[r];
	        arr[r] = t;
	    }
	    
	    return arr;
	}
	
	public static Bitmap scrambleImage(Bitmap image, int piecesPerLine) {
	    int px = (int)Math.ceil(image.getWidth() / (double)piecesPerLine);
	    int ph = (int)Math.ceil(image.getHeight() / (double)piecesPerLine);
	    int count = piecesPerLine * piecesPerLine;
	    int[] arr = shuffle(arrayRange(count));


		Config config = image.getConfig() == null ? Bitmap.Config.ARGB_8888 : image.getConfig();
		Bitmap destBmp = Bitmap.createBitmap(image.getWidth(), image.getHeight(), config);
		Canvas canvas = new Canvas(destBmp);

	    for (int i = 0; i < piecesPerLine; i++) {
	        for (int j = 0; j < piecesPerLine; j++) {
	            int n = arr[i * piecesPerLine +  j];
	            int row = n / piecesPerLine;
	            int col = n % piecesPerLine;
	            
	            Rect fromRect = new Rect(row * px, col * ph, row * px + px, col * ph + ph);
	            Rect toRect = new Rect(j * px, i * ph, j * px + px, i * ph + ph);
	            
	            canvas.drawBitmap(image, fromRect, toRect, null);
	        }
	    }

	    return destBmp;
	}

	public static Bitmap readImage(String imageUrl) throws IOException {
    	URL url = new URL(imageUrl);
    	HttpURLConnection connection  = (HttpURLConnection) url.openConnection();

    	InputStream is = connection.getInputStream();
    	return BitmapFactory.decodeStream(is);  
    }

}
