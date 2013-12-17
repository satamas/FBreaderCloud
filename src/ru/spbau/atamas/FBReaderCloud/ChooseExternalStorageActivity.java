package ru.spbau.atamas.FBReaderCloud;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;

public class ChooseExternalStorageActivity extends Activity{
	private static ListView     listView;
	private static ArrayAdapter<String> storages;
	private static Navigator drive;
	static final int REQUEST_ACCOUNT_PICKER = 1;
	static final int REQUEST_AUTHORIZATION = 2;
	private Context context;

	@Override
	final protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_external_storage);
		listView = (ListView) findViewById(R.id.ListView02);
		context = getApplicationContext();
		storages = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,
				                            new String[] {"Google Drive", "Place for your external storage"});
		listView.setAdapter(storages);
		
		
		
		OnItemClickListener messageClickedHandler = new OnItemClickListener() 
		{
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
			{
				if(position == 0){
					drive = new GoogleDriveNavigator(context, ChooseExternalStorageActivity.this);
				}
			}
		};
		listView.setOnItemClickListener(messageClickedHandler); 
	}
	
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		switch (requestCode) {
		case REQUEST_ACCOUNT_PICKER:
			if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
				String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
				if (accountName != null) {
					drive.setSelectedAccountName(accountName);
					startShowContentActivity();
				}
			}
			break;
		case REQUEST_AUTHORIZATION:
			if (resultCode == Activity.RESULT_OK) {
				//account already picked
			} else {
				Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{ GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE },
						true, null, null, null, null);
				startActivityForResult(intent, REQUEST_ACCOUNT_PICKER);
			}
			break;
		}
	}
	
	
	public static Navigator getNavigator(){
		return drive;
	}
	
	public void startShowContentActivity(){
	    Intent intent = new Intent(getApplicationContext(), ShowDriveContentActivity.class);
	    startActivity(intent);
	}

}
