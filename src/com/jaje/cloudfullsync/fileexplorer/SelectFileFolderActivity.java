package com.jaje.cloudfullsync.fileexplorer;

import java.io.File;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.jaje.cloudfullsync.R;

public class SelectFileFolderActivity extends FileExplorerActivity {

	private int whatSelect;

	public static final int DIR_SELECT = 1;
	public static final int FILE_SELECT = 2;
	public static final String WHAT_TO_SELECT = "requestCode";
	public static final String RESULT = "result";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		whatSelect = getIntent().getExtras().getInt(WHAT_TO_SELECT);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

		File selected = new File(((FileListItem) getListAdapter().getItem(
				info.position)).getPath());

		if (((whatSelect == DIR_SELECT) && (selected.isDirectory()))
				|| ((whatSelect == FILE_SELECT) && (selected.isFile()))) {
			menu.add(Menu.NONE, R.id.selectMenuItem, Menu.FIRST, R.string.select);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		if (item.getItemId() == R.id.selectMenuItem) {
			Intent returnIntent = new Intent();
			returnIntent.putExtra(RESULT,
					((FileListItem) getListAdapter().getItem(info.position))
							.getPath());
			returnIntent.putExtras(getIntent());
			setResult(RESULT_OK, returnIntent);
			finish();
			return true;
		} else {
			return super.onContextItemSelected(item);
		}
	}
}
