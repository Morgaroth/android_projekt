package com.jaje.cloudfullsync.fileexplorer;

import android.os.Bundle;

public class SelectFolderActivity extends SelectFileFolderActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getIntent().putExtra(SelectFileFolderActivity.WHAT_TO_SELECT,
				SelectFileFolderActivity.DIR_SELECT);
		super.onCreate(savedInstanceState);		
	}
}
