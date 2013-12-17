package ru.spbau.atamas.FBReaderCloud;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;


public class GoogleDriveNavigator extends Navigator {
	static final int REQUEST_ACCOUNT_PICKER = 1;
	static final int REQUEST_AUTHORIZATION = 2;
	private static Drive 	service;
	private Activity activity;
	private GoogleAccountCredential credential;
	private List<File>		FolderContents;
	private List<String> 	parents;

	GoogleDriveNavigator(Context context, Activity activity){
		this.activity = activity;
		credential = GoogleAccountCredential.usingOAuth2(context, Arrays.asList(DriveScopes.DRIVE));
		Intent authIntent = AccountPicker.newChooseAccountIntent(null, null, new String[]{ GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE },
				true, null, null, null, null);
		activity.startActivityForResult(authIntent, REQUEST_ACCOUNT_PICKER);
		parents = new ArrayList<String>();
		parents.add("root");
	}
	
	public void setSelectedAccountName(String accountName){
		credential.setSelectedAccountName(accountName);
		service = getDriveService(credential);
	}
	
	
	public Boolean inRoot(){
		if(parents.size() == 1){
			return true;
		} else{
			return false;
		}
	}

	public void addParent(String title){
		for(File tmp : FolderContents){
			if(tmp.getTitle().equals(title)){
				parents.add(tmp.getId());
			}
		}
	}

	public void removeParent(){
		if(parents.size() > 1){
			parents.remove(parents.size() - 1);
		}
	}


	public List<FileInfo> getDriveContents() {
		List<FileInfo>  FileInfoArray;
		FolderContents = new ArrayList<File>();
		FileInfoArray = new ArrayList<FileInfo>();
		com.google.api.services.drive.Drive.Files.List request = null;         
		do{
			try{ 
				request = service.files().list();
				request.setQ("'"+parents.get(parents.size()-1)+"' in parents and trashed = false");
				com.google.api.services.drive.model.FileList fileList = request.execute();
				FolderContents.addAll(fileList.getItems());
				request.setPageToken(fileList.getNextPageToken());
			} catch (UserRecoverableAuthIOException e) {
				e.printStackTrace();
				activity.startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
			}catch (IOException e) {
				e.printStackTrace();
				if (request != null){
					request.setPageToken(null);
				}
			}
		} while (request.getPageToken() !=null && request.getPageToken().length() > 0);

		for(File tmp : FolderContents){
			if(tmp.getMimeType().equals("application/vnd.google-apps.folder")){
				String name = tmp.getTitle();
				FileInfoArray.add(new FileInfo(name, true));
			}else{
				String name = tmp.getTitle();
				FileInfoArray.add(new FileInfo(name, false));
			}
		}

		return FileInfoArray;
	}

	public InputStream getFileStream(String title){
		InputStream iStream = null;
		for(File tmp : FolderContents){
			if(tmp.getTitle().equals(title)){
				if (tmp.getDownloadUrl() != null && tmp.getDownloadUrl().length() >0){
					try{
						com.google.api.client.http.HttpResponse resp = service.getRequestFactory()
								.buildGetRequest(new GenericUrl(tmp.getDownloadUrl()))
								.execute();
						iStream = resp.getContent();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return iStream;
	}


	private Drive getDriveService(GoogleAccountCredential credential) {
		return new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential).build();
	}
}
