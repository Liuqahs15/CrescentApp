package com.example.crescentcameraapp;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	File photoFile;
	Button takePhotoBTN;
	Button uploadPhotoBTN;
	Button choosePhotoBTN;
	ImageView imageViewIV;
	TextView photoStatusTV;
	final int PICK_IMAGE = 0;
	final int TAKE_PHOTO = 1;
	static final String TAG = "MainActivity";
	Bitmap imageBMP;
	ProgressDialog dialog;

	String filePath = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		photoStatusTV = (TextView) findViewById(R.id.photoStatus);
		imageViewIV = (ImageView) findViewById(R.id.showImage);
		takePhotoBTN = (Button) findViewById(R.id.takePhoto);
		uploadPhotoBTN = (Button) findViewById(R.id.uploadButton);
		choosePhotoBTN = (Button) findViewById(R.id.chooseButton);
		
			//Upload photo onclick event
		uploadPhotoBTN.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(imageBMP == null){
					Toast.makeText(getApplicationContext(), "No image selected", Toast.LENGTH_LONG)
					.show();
				}
				dialog = ProgressDialog.show(MainActivity.this, "Uploading ...",
						 "Please wait ...", true);
				new ImageUploadTask().execute();
			}
			
		});

			//take photo onclick event
		takePhotoBTN.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					Intent photoIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(photoIntent, TAKE_PHOTO);
					ChooseAPhoto();
				}//end of try
				catch (Exception e){
	                Toast.makeText(getApplicationContext(),
	                        "Error choosing image",
	                        Toast.LENGTH_LONG).show();
	                Log.e(e.getClass().getName(), e.getMessage(), e);					
				}
			}// onClick()
		});
		
		choosePhotoBTN.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ChooseAPhoto();
			}			
		});
		
	}// onCreate
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		System.gc();
	}
	
	private void printMemInfoToLog(){
		ActivityManager manager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo mInfo = new ActivityManager.MemoryInfo();
		manager.getMemoryInfo(mInfo);
		Log.i(TAG, "AVAILABLE MEM: " + mInfo.availMem);
		Log.i(TAG, "LOW MEM?: " + mInfo.lowMemory);
		Log.i(TAG, "THRESHOLD: " + mInfo.threshold);
	}
	
	private void TryToUploadPhoto(){
	       String exsistingFileName = filePath;

	        String lineEnd = "\r\n";
	        String twoHyphens = "--";
	        String boundary = "*****";
	    try {
	            // ------------------ CLIENT REQUEST

	            Log.e("UPLOADING PHOTO", "Inside second Method");

	            FileInputStream fileInputStream = new FileInputStream(new File(
	                    exsistingFileName));

	            // open a URL connection to the Servlet

	            URL url = new URL("http://149.151.162.87/test.php");

	            // Open a HTTP connection to the URL
	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

	            // Allow Inputs
	            conn.setDoInput(true);

	            // Allow Outputs
	            conn.setDoOutput(true);

	            // Don't use a cached copy.
	            conn.setUseCaches(false);

	            // Use a post method.
	            conn.setRequestMethod("POST");

	            conn.setRequestProperty("Connection", "Keep-Alive");

	            conn.setRequestProperty("Content-Type",
	                    "multipart/form-data;boundary=" + boundary);

	            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

	            //dos.writeBytes(twoHyphens + boundary + lineEnd);
	            dos.writeBytes("Content-Disposition: post-data; name=uploadedfile;filename=" + exsistingFileName);
	           // dos.writeBytes(lineEnd);

	            Log.e("HEADER ERROR? ", "Headers are written");

	            // create a buffer of maximum size

	            int bytesAvailable = fileInputStream.available();
	            int maxBufferSize = 1000;
	            // int bufferSize = Math.min(bytesAvailable, maxBufferSize);
	            byte[] buffer = new byte[bytesAvailable];

	            // read file and write it into form...

	            int bytesRead = fileInputStream.read(buffer, 0, bytesAvailable);

	            while (bytesRead > 0) {
	                dos.write(buffer, 0, bytesAvailable);
	                bytesAvailable = fileInputStream.available();
	                bytesAvailable = Math.min(bytesAvailable, maxBufferSize);
	                bytesRead = fileInputStream.read(buffer, 0, bytesAvailable);
	            }

	            // send multipart form data necesssary after file data...

	            //dos.writeBytes(lineEnd);
	           // dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

	            // close streams
	            Log.e("FILE WAS WRITTEN ", "File is written");
	            fileInputStream.close();
	            dos.flush();
	            dos.close();


	        try {
	            BufferedReader rd = new BufferedReader(new InputStreamReader(conn
	                    .getInputStream()));
	            String line;
	            while ((line = rd.readLine()) != null) {
	                Log.e("Dialoge Box", "Message: " + line);
	            }
	            rd.close();

	        } catch (IOException ioex) {
	            Log.e("MediaPlayer", "error: " + ioex.getMessage(), ioex);
	        }
		//end of outer try{}
	    } catch (MalformedURLException ex) {
	    	Log.e("FILE ERROR ", "error: " + ex.getMessage(), ex);
	    }
	    
	    catch (IOException ioe) {
	    	Log.e("I don't fuckin know: ", "error: " + ioe.getMessage(), ioe);
	    }
	}
	
    private void ChooseAPhoto(){
    	//This will display a message in the UI if something goes wrong
    	Runnable uiAction = new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(),
						"Error choosing image",
						Toast.LENGTH_LONG).show();
			}    		
    	};
    	
		try {
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(
					Intent.createChooser(intent, "Select Picture"),
					PICK_IMAGE);
		} catch (Exception e) {
			runOnUiThread(uiAction);
			Log.e(e.getClass().getName(), e.getMessage(), e);
		}
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    		// Handle item selection
    		switch (item.getItemId()) {
    		case R.id.gallery1:
    			ChooseAPhoto();
    			return true;
    		default:
    			return super.onOptionsItemSelected(item);
    		}
    }

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		printMemInfoToLog();
		if (requestCode == 0) {
			if (resultCode == Activity.RESULT_OK) {
				Uri selectedImageUri = data.getData();

				// Set the image view and photoStatus stuff.
				// this part is my code, not copy/pasted
				/*
				 * Bitmap theImage = (Bitmap) data.getExtras().get("data");
				 * imageViewIV.setImageBitmap(theImage);
				 * photoStatusTV.setText(R.string.photo_taken_text);
				 */

				try {
					// OI FILE Manager
					String filemanagerstring = selectedImageUri.getPath();

					// MEDIA GALLERY
					String selectedImagePath = getPath(selectedImageUri);

					if (selectedImagePath != null) {
						filePath = selectedImagePath;
					} else if (filemanagerstring != null) {
						filePath = filemanagerstring;
					} else {
						Toast.makeText(getApplicationContext(),
								"Unknown path for image", Toast.LENGTH_LONG)
								.show();
						Log.e("Bitmap", "Unknown path for image");
					}

					if (filePath != null) {
						decodeFile(filePath);
					} else {
						imageBMP = null;
					}
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "Internal error",
							Toast.LENGTH_LONG).show();
					Log.e(e.getClass().getName(), e.getMessage(), e);
				}// end of catch
			}// end of if(resultCode)
		}// end of if(requestCode == 0)
		else if(requestCode == 1){
			Bitmap theImage = (Bitmap) data.getExtras().get("data"); 
			imageViewIV.setImageBitmap(theImage);
			photoStatusTV.setText(R.string.photo_taken_text);			
		}
	}// onActivityResult

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(uri, projection, null, null,
				null);
		if (cursor != null) {
			// HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
			// THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} else {
			Log.d("ERROR: ", "FILE PATH NOT FOUND ON PHONE");
			return null;
		}
	}// end of getPath()

	public void decodeFile(String filePath) {
		// Decode image size
		printMemInfoToLog();
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		photoFile = new File(filePath);
		if(BitmapFactory.decodeFile(filePath, o) == null){
			Log.d("DECODING: ", "Error! The file is null in the decoding code!");
		}

		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = 1;
		imageBMP = BitmapFactory.decodeFile(filePath, o2);

		if (imageBMP != null) {
			imageViewIV.setImageBitmap(imageBMP);
		}
		else{
			Resources res = getResources();
			imageViewIV.setImageDrawable( res.getDrawable(R.drawable.noimage) );
		}
	}// end of decodeFile()
	
	class ImageUploadTask extends AsyncTask <Void, Void, String>{
        @SuppressWarnings("deprecation")
        
		
		private Runnable fileErrorUiThreadAction = new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
                if (dialog.isShowing())
                    dialog.dismiss();
                Toast.makeText(getApplicationContext(),
                        "Error sending file to server...",
                        Toast.LENGTH_LONG)
                        	.show();				
			}        	
        };
        
        private Runnable uploadCompletedUiThreadAction = new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), 
							   "Upload completed.", 
							   Toast.LENGTH_LONG)
							   	.show();		
				if(dialog.isShowing()){
					dialog.cancel();
				}
			}        	
        };
		
        @SuppressWarnings("deprecation")
		protected String doInBackground(Void... unsued) {        	

        		try {

	        		ByteArrayOutputStream bos = new ByteArrayOutputStream();
	
	        		Bitmap bitmap = imageBMP;
	
	        		bitmap.compress(CompressFormat.JPEG, 100, bos);
	
	        		byte[] data = bos.toByteArray();
	
	        		HttpClient httpClient = new DefaultHttpClient();
	
	        		HttpPost postRequest = new HttpPost("http://149.151.162.87/test.php");
	
	        		String fileName = "newImage.jpg";
	
	        		ByteArrayBody bab = new ByteArrayBody(data, fileName);
	
	        		ContentBody mimePart = bab;
	        		
	        		FileBody photoBody = new FileBody(photoFile);
	
	        		// File file= new File("/mnt/sdcard/forest.png”);
	
	        		// FileBody bin = new FileBody(file);
	        		Log.d("BAB AS STRING: ", bab.toString());
	
	        		MultipartEntity reqEntity = new MultipartEntity();
	
	        		reqEntity.addPart("file", mimePart);
	
	        		postRequest.setEntity(reqEntity);
	
	        		int timeoutConnection = 60000;
	
	        		HttpParams httpParameters = new BasicHttpParams();
	
	        		HttpResponse response = httpClient.execute(postRequest);
	
	        		BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
	
	        		String sResponse;
	
	        		StringBuilder s = new StringBuilder();
	
	        		while ((sResponse = reader.readLine()) != null) {
	
	        			s = s.append(sResponse);
	        		}
	        		
	        		Log.d("RESPONSE: ", "" + s);

        		} catch (Exception e){
        			Log.d("ERROR SENDING TO SERVER: ", e.getMessage());
        		}
        		return "YES";
        }
        
        protected void onPostExecute(String sResponse) {  	
        	//If it's successful, post this Toast message to the UI.
        	runOnUiThread(uploadCompletedUiThreadAction);
        }//onPostExecute()
	}//end of class ImageUploadTask
}	// end of MainActivity
