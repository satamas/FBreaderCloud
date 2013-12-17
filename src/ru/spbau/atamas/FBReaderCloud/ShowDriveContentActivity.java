package ru.spbau.atamas.FBReaderCloud;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class ShowDriveContentActivity extends Activity {

	private static Navigator drive;
	private static ListView     listView;
	private static ArrayAdapter<String> Adapter;
	private static List<FileInfo>  fileArr;
	private static String[] FileArray;
	private ProgressDialog pd;
	private static Integer[] imageId;

	@Override
	final protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		drive = ChooseExternalStorageActivity.getNavigator();
		listView = (ListView) findViewById(R.id.ListView01);
		if(Adapter != null){
			listView.setAdapter(Adapter);
		}else{
			new PopulateListView().execute();
		}

		OnItemClickListener messageClickedHandler = new OnItemClickListener() 
		{
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
			{
				String title = FileArray[position];
				
				if(imageId[position] == R.drawable.ic_folder_close){
					drive.addParent(title);
					new PopulateListView().execute();
				} else{
					new storeFile().execute(title);
				}
			}
		};
		listView.setOnItemClickListener(messageClickedHandler); 


	}
	

public class storeFile extends AsyncTask<String, Void, Void>{
			private InputStream iStream;
			private OutputStream oStream;
			private java.io.File file;
			
		    @Override
		    protected void onPreExecute() {
		    	super.onPreExecute();
		    	pd = new ProgressDialog(ShowDriveContentActivity.this,android.R.style.Theme_Black);
		    	pd.setTitle("Downloading file");
		        pd.setMessage("Please wait...");
		        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		        pd.setMax(100);
		        pd.setIndeterminate(true);
		        pd.show();
		    }
			
		    protected Void doInBackground(String... titles) {
		    	for(String title : titles){
					file = new java.io.File(Environment
							.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath(), 
							title);
			    	iStream = drive.getFileStream(title);
					try 
					{
						oStream = new FileOutputStream(file);
						try
						{
							try
							{
								final byte[] buffer = new byte[1024];
								int read;
								int size = iStream.available();
								if(size < 1024){
									size = 1024;
								}
								pd.setIndeterminate(false);
								while ((read = iStream.read(buffer)) != -1)
								{
									oStream.write(buffer, 0, read);
									pd.incrementProgressBy(1024/size);
								}
								oStream.flush();
							} finally {
								oStream.close();
								iStream.close();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
		    	}
		    	return null;
		    }
		    
			@Override
		    protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				try{
					startActivity(
						new Intent("android.intent.action.VIEW", Uri.fromFile(file))
						);
				} catch(ActivityNotFoundException activityNotFound){
					Toast.makeText(getApplicationContext(), "File was downloaded, but it can't be opened by FBreader",
							   Toast.LENGTH_LONG).show();
				}
				pd.dismiss();
		    }
}

	private class PopulateListView extends AsyncTask<Void, Void, Void>{
	    @Override
	    protected void onPreExecute() {
	    	super.onPreExecute();
	    	pd = new ProgressDialog(ShowDriveContentActivity.this,android.R.style.Theme_Black);
	    	pd.setTitle("Downloading content");
	        pd.setMessage("Please wait...");
	        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	        pd.setIndeterminate(true);
	        pd.show();
	    }

	    protected Void doInBackground(Void... params) {
	    	fileArr  = drive.getDriveContents();
			
			FileArray = new String [fileArr.size()];
		    imageId = new Integer [fileArr.size()];
			int i = 0;
			int j = fileArr.size() - 1;
			
			for(FileInfo tmp : fileArr){
				if(tmp.isFolder()){
					FileArray[i] = tmp.getName();
					imageId[i] = R.drawable.ic_folder_close;
					++i;
				} else{
					FileArray[j] = tmp.getName();
					imageId[j] = R.drawable.ic_cloud_download;
					--j;
				}
			}
	      return null;
	    }

	    @Override
	    protected void onPostExecute(Void result) {
	      super.onPostExecute(result);
			Adapter = new FileList(ShowDriveContentActivity.this , FileArray, imageId);
			listView.setAdapter(Adapter);
			pd.dismiss();
	    }
	}

	@Override
	public void onBackPressed(){
		if(!drive.inRoot()){
			drive.removeParent();
			new PopulateListView().execute();
		} else{
			super.onBackPressed();
		}
	}
	
}
