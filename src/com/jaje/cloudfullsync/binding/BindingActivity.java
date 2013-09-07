package com.jaje.cloudfullsync.binding;

import jaje.android.lib.dialogs.MyDialogs;
import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.jaje.cloudfullsync.App;
import com.jaje.cloudfullsync.R;
import com.jaje.cloudfullsync.db.DBAdapter;

public class BindingActivity extends ListActivity implements
		MenuItem.OnMenuItemClickListener {

	Bindings bindings;
	DBAdapter db;
	Binding ac;
	Button addButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.binding_layout);
		// Show the Up button in the action bar.
		setupActionBar();
		bindings = App.getBindings();
		db = new DBAdapter(this);
		registerForContextMenu(getListView());
		// addButton.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// }
		// });
		// bindings.add(CloudDrive.Dropbox, "loca1/dupcia", "virtual1/ja",
		// SyncDirect.Bidirect, true, "5");
		// bindings.add(CloudDrive.Skydrive, "loca453/nozka", "virtuafds/fghgf",
		// SyncDirect.OnlyDownload, true, "1");
		// bindings.add(CloudDrive.GoogleDrive, "lo/ddsafgdsupcia", "vir/jadsa",
		// SyncDirect.OnlyUpload, false, "10");
		refreshList();

	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.binding_settings_menu, menu);
		menu.findItem(R.id.add_binding_button).setOnMenuItemClickListener(this);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void refreshList() {
		setListAdapter(new BindListAdapter(this, R.layout.binding_item_layout,
				bindings.bindingsList));
	}

	@Override
	public boolean onMenuItemClick(MenuItem v) {
		startActivity(new Intent(this, AddBindingActivity.class));
		return false;
	}

	/*
	 * CloudDrive[] drives = CloudDrive.values(); final String[] options = new
	 * String[drives.length]; for (int i = 0; i < drives.length; ++i) {
	 * options[i] = drives[i].name(); } MyDialogs.SingleChoice.show(this,
	 * "Select cloud source:", options, new OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) {
	 * Binding bind = new Binding(CloudDrive .valueOf(options[which]),
	 * getString(R.string.none), getString(R.string.none), SyncDirect.Bidirect,
	 * false, "10"); bindings.add(bind); BindingActivity.this.refreshList();
	 * BindingActivity.this.getListAdapter().getView( bindings.getIndexOf(bind),
	 * null, null); } });
	 */
	@Override
	protected void onPause() {
		db.open().saveBindings(bindings).close();
		super.onPause();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.binding_context_menu, menu);

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		Binding binding = (Binding) getListAdapter().getItem(info.position);
		if (binding.isDeactive()) {
			MenuItem menuItem = menu
					.findItem(R.binding_context_menu.de_activate);
			menuItem.setTitle(R.string.activate);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		Binding actual = (Binding) getListAdapter().getItem(info.position);
		switch (item.getItemId()) {
		case R.binding_context_menu.edit:
			Intent intent = new Intent().setClass(this,
					BindingPreferences.class).putExtra(
					BindingPreferences.BINDING_INDEX,
					App.getBindings().bindingsList.indexOf(actual));
			startActivityForResult(intent, 1);
			break;
		case R.binding_context_menu.de_activate:
			bindings.bindingsList.get(info.position).swith();
			break;
		case R.binding_context_menu.del:
			MyDialogs.Confirm.show(this,
					getString(R.string.are_you_sure_to_delete), "",
					R.string.yes, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							bindings.removeBinding(bindings.bindingsList
									.get(info.position));
							BindingActivity.this.refreshList();
						}
					}, R.string.no, null);
			break;
		default:
			System.out.println("unexpected option");
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		refreshList();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		openContextMenu(v);
	}
}
