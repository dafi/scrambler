package com.dafi.scambler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class ScramblerActivity extends Activity {
    /** Called when the activity is first created. */
	Bitmap scrambledImage;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        try {
			Bitmap image = readImage("http://farm3.staticflickr.com/2457/3697466514_721bd9c533_d.jpg");
	        ImageView imageView = (ImageView) findViewById(R.id.scramblerImage);
	        scrambledImage = ScramblerUtils.scrambleImage(image, 3);
	        imageView.setImageBitmap(scrambledImage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public Bitmap readImage(String imageUrl) throws IOException {
    	URL url = new URL(imageUrl);
    	HttpURLConnection connection  = (HttpURLConnection) url.openConnection();

    	InputStream is = connection.getInputStream();
    	return BitmapFactory.decodeStream(is);  
    }
}