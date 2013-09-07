package com.jaje.cloudfullsync;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.jaje.cloudfullsync.binding.BindingActivity;
import com.jaje.cloudfullsync.fileexplorer.FileExplorerActivity;
import com.jaje.cloudfullsync.fileexplorer.SelectFileActivity;
import com.jaje.cloudfullsync.fileexplorer.SelectFileFolderActivity;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_settings_menu, menu);
		return true;
	}

	public void runFileExplorator(View v) {
		Intent i = new Intent(this, FileExplorerActivity.class);
		startActivity(i);
	}

	public void runBinding(View v) {
		Intent i = new Intent(this, BindingActivity.class);
		startActivity(i);
	}

	public void runManagingAccounts(View v) {
		Log.d("INFO", "run managing accounts");
	}

	public void selectFile(View v) {
		Intent i = new Intent(this, SelectFileActivity.class);
		startActivityForResult(i, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			System.out.println(data.getExtras().get(
					SelectFileFolderActivity.RESULT));
		} else {
			System.out.println("result failure");
		}
	}
}
