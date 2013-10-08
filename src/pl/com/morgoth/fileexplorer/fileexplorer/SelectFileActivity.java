package pl.com.morgoth.fileexplorer.fileexplorer;

import android.os.Bundle;

public class SelectFileActivity extends SelectFileFolderActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getIntent().putExtra(SelectFileFolderActivity.WHAT_TO_SELECT,
				SelectFileFolderActivity.FILE_SELECT);
		super.onCreate(savedInstanceState);		
	}
}
