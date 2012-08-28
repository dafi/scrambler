package com.dafi.scambler;

// http://stackoverflow.com/questions/2661536/screenshot-android
// http://www.vogella.com/articles/AndroidPerformance/article.html
// http://developer.android.com/training/sharing/receive.html
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ScramblerActivity extends Activity {
	private static final int SAVE_IMAGE = 0;
    private static final int SHARE_IMAGE = 1;

    /** Called when the activity is first created. */
	Bitmap originalImage;
	Bitmap scrambledImage;

	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		// Get intent, action and MIME type
	    Intent intent = getIntent();
	    String action = intent.getAction();
	    String type = intent.getType();

	    if (Intent.ACTION_SEND.equals(action) && type != null) {
	        if (type.startsWith("image/")) {
	            handleSendImage(intent); // Handle single image being sent
	        }
	    } else {
	        // started from home screen
	        // Since 3.0 networking operations aren't allowed on main thread
	        // so we read using an AsyncTask
	        new ReadImageAsyncTask().execute(new String[] {"http://farm3.staticflickr.com/2457/3697466514_721bd9c533_d.jpg"});
	    }
    }
    
	void handleSendImage(Intent intent) {
	    Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
	    if (imageUri != null) {
	    	try {
	    		Bitmap image = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
				showImage(image);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}

	public Bitmap readImage(String imageUrl) throws IOException {
    	URL url = new URL(imageUrl);
    	HttpURLConnection connection  = (HttpURLConnection) url.openConnection();

    	InputStream is = connection.getInputStream();
    	return BitmapFactory.decodeStream(is);  
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveImage:
            	promptFileName(SAVE_IMAGE);
            	break;
            case R.id.shareImage:
            	promptFileName(SHARE_IMAGE);
            	break;
            case R.id.scrambleImage:
            	scramble();
            	break;
        }
        return true;
    }

    private void promptFileName(final int operation) {
    	final EditText input = new EditText(this);
    	input.setText("scrambleApp");
    	input.selectAll();
    	
    	new AlertDialog.Builder(this)
        .setTitle("Update Status")
        .setMessage("message")
        .setView(input)
        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String fileName = input.getText().toString();
                if (operation == SAVE_IMAGE) {
                	saveImage(fileName);
                } else if (operation == SHARE_IMAGE) {
                	shareImage(fileName);
                }
            }
    	})
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do nothing.
            }
        })
        .show();
    }

    private void shareImage(String fileName) {
    	File imageFile = saveImage(fileName);

		ContentValues values = new ContentValues(2);
	    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
	    values.put(MediaStore.Images.Media.DATA, imageFile.getAbsolutePath());
	    // sharing on google plus works only using MediaStore 
	    Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("image/jpeg");
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Scrambled image");
		sharingIntent.putExtra(android.content.Intent.EXTRA_STREAM, uri);
		
		startActivity(Intent.createChooser(sharingIntent, "Share via"));
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	private File saveImage(String fileName) {
    	String imagePath = Environment.getExternalStorageDirectory().toString() + "/" + fileName + ".jpg";   
    	OutputStream fout = null;
    	File imageFile = new File(imagePath);

    	try {
    	    fout = new FileOutputStream(imageFile);

    	    scrambledImage.compress(Bitmap.CompressFormat.JPEG, 100, fout);

    	    fout.flush();
    	    fout.close();

    	} catch (FileNotFoundException e) {
    	    // TODO Auto-generated catch block
    	    e.printStackTrace();
    	} catch (IOException e) {
    	    // TODO Auto-generated catch block
    	    e.printStackTrace();
    	}    	
    	Toast.makeText(this, "Image saved on " + imagePath, Toast.LENGTH_LONG).show();
    	
    	return imageFile;
	}
	
	private void showImage(Bitmap image) {
		originalImage = image;
		scramble();
	}

	private void scramble() {
		ImageView imageView = (ImageView) findViewById(R.id.scramblerImage);
		scrambledImage = ScramblerUtils.scrambleImage(originalImage, 3);
		imageView.setImageBitmap(scrambledImage);
	}
	
	private class ReadImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... url) {
	        try {
				return readImage(url[0]);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap image) {
			showImage(image);
		}		
	}
}