package com.dafi.scambler;

// http://stackoverflow.com/questions/2661536/screenshot-android
// http://www.vogella.com/articles/AndroidPerformance/article.html
// http://developer.android.com/training/sharing/receive.html
import java.io.File;
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
    private static final String JPEG_MIME = "image/jpeg";
	private static final int SELECT_PHOTO = 0;
	/** Called when the activity is first created. */
	Bitmap originalImage;
	Bitmap scrambledImage;
	private int piecesPerLine = 3;

	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        registerForContextMenu(findViewById(R.id.scramblerImage));

		// Get intent, action and MIME type
	    Intent intent = getIntent();
	    String action = intent.getAction();
	    String type = intent.getType();

	    if (Intent.ACTION_SEND.equals(action) && type != null) {
	        if (type.startsWith("image/")) {
	            handleSendImage(intent); // Handle single image being sent
	        }
	    } else {
	    	if (originalImage == null) {
		        new ReadImageAsyncTask().execute(new String[] {"http://farm3.staticflickr.com/2457/3697466514_721bd9c533_d.jpg"});
	    	}
	    }
    }

	public void onCreateContextMenu(android.view.ContextMenu menu, android.view.View v, android.view.ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(Menu.NONE, R.id.chooseImage, Menu.NONE, R.string.choose);
		menu.add(Menu.NONE, R.id.saveImage, Menu.NONE, R.string.saveImage);
		menu.add(Menu.NONE, R.id.shareImage, Menu.NONE, R.string.shareImage);
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
            	promptFileName(R.id.saveImage);
            	break;
            case R.id.shareImage:
            	promptFileName(R.id.shareImage);
            	break;
            case R.id.scrambleImage:
            	scramble();
            	break;
            case R.id.chooseImage:
            	chooseImage();
            	break;
            case R.id.pieces:
            	promptPieces();
            	break;
            	
        }
        return true;
    }
    
    private void promptPieces() {
    	final EditText input = new EditText(this);
    	input.setText(String.valueOf(piecesPerLine));
    	input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
    	input.selectAll();
    	
    	new AlertDialog.Builder(this)
        .setTitle(R.string.choosePiecesNumber)
        .setView(input)
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String text = input.getText().toString();
            	try {
					int pieces = Integer.parseInt(text);
                    if (pieces > 1) {
                    	piecesPerLine = pieces;
                    	scramble();
                    }
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(),
							getString(R.string.invalidNumber, text),
							Toast.LENGTH_LONG).show();
				}
            }
    	})
        .show();
	}

	private void chooseImage() {
    	Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
    	photoPickerIntent.setType("image/*");
    	startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    protected void onActivityResult(int requestCode, int resultCode, 
    	       Intent imageReturnedIntent) {
    	super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 

	    switch(requestCode) { 
	    	case SELECT_PHOTO:
			if (resultCode == RESULT_OK) {
				try {
					Uri selectedImage = imageReturnedIntent.getData();
					InputStream imageStream = getContentResolver()
							.openInputStream(selectedImage);
					showImage(BitmapFactory.decodeStream(imageStream));
				} catch (Exception e) {
					Toast.makeText(
							this,
							getString(R.string.errorReadingImage,
									e.getMessage()), Toast.LENGTH_LONG).show();
				}
			}
	    }
	}
    
    
	public boolean onContextItemSelected (MenuItem item) {
    	return onOptionsItemSelected(item);
    }

    private void promptFileName(final int id) {
    	final EditText input = new EditText(this);
    	input.setText("scrambleApp");
    	input.selectAll();
    	
    	new AlertDialog.Builder(this)
        .setTitle(R.string.fileName)
        .setView(input)
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String fileName = input.getText().toString();
                if (id == R.id.saveImage) {
                	saveImage(fileName);
                } else if (id == R.id.shareImage) {
                	shareImage(fileName);
                }
            }
    	})
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do nothing.
            }
        })
        .show();
    }

    private void shareImage(String fileName) {
    	File imageFile = saveImage(fileName);

		ContentValues values = new ContentValues(2);
	    values.put(MediaStore.Images.Media.MIME_TYPE, JPEG_MIME);
	    values.put(MediaStore.Images.Media.DATA, imageFile.getAbsolutePath());
	    // sharing on google plus works only using MediaStore 
	    Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType(JPEG_MIME);
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.scrambledImage));
		sharingIntent.putExtra(android.content.Intent.EXTRA_STREAM, uri);
		
		startActivity(Intent.createChooser(sharingIntent, getString(R.string.shareVia)));
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
        	Toast.makeText(this, getString(R.string.imageSavedOn, imagePath), Toast.LENGTH_LONG).show();
    	} catch (Exception e) {
        	Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
    		
    	    e.printStackTrace();
    	}    	
    	
    	return imageFile;
	}
	
	private void showImage(Bitmap image) {
		originalImage = image;
		scramble();
	}

	private void scramble() {
		ImageView imageView = (ImageView) findViewById(R.id.scramblerImage);
		scrambledImage = ScramblerUtils.scrambleImage(originalImage, piecesPerLine);
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