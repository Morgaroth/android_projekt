package com.jaje.cloudfullsync.fileexplorer;

import jaje.android.lib.collections.ObjectCollection;
import jaje.android.lib.dialogs.MyDialogs;
import jaje.android.lib.files.MyFiles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jaje.cloudfullsync.MyConfiguration;
import com.jaje.cloudfullsync.R;

public class FileExplorerActivity extends ListActivity implements
		MenuItem.OnMenuItemClickListener {

	private static final int MOVE = 1;
	private static final int COPY_FOLDER = 2;
	private static final int COPY_FILE = 3;
	private static final String SOURCE_FILE_PATH = "sourcefile";
	private static final String FILE_TYPE = "file";
	private static final String DIR_TYPE = "dir";
	private static final String UP_FOLDER_NAME = "up_name";
	private static final String ITEMS_IN_VIEWING_LIST = "items";
	private static final String ITEMS_MANAGER = "manager";
	private static final String ROOT_DIR = "root";
	private static final String PARENT_DIR = "parent";
	private static final String CURRENT_DIR = "current";

	private TextView pathView;
	private ArrayList<FileListItem> dirs;
	private ArrayList<FileListItem> fls;
	private ArrayList<FileListItem> sum;
	private String rootDir;
	private String parentPath;
	private String currentPath;

	private ObjectCollection<FileListItem> itemManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_explorer_layout);
		registerForContextMenu(getListView());
		dirs = new ArrayList<FileListItem>();
		fls = new ArrayList<FileListItem>();

		((TextView) findViewById(R.id.up_name)).setText("UP");
		ImageView imageCity = (ImageView) findViewById(R.id.up_img);
		int imageResource = getResources().getIdentifier("drawable/up", null,
				getPackageName());
		Drawable image = getResources().getDrawable(imageResource);
		imageCity.setImageDrawable(image);
		pathView = (TextView) findViewById(R.id.up_folder);

		if (savedInstanceState != null) {
			rootDir = savedInstanceState.getString(ROOT_DIR);
			parentPath = savedInstanceState.getString(PARENT_DIR);
			currentPath = savedInstanceState.getString(CURRENT_DIR);

			System.out.println("parentpath = " + parentPath);
			sum = savedInstanceState
					.getParcelableArrayList(ITEMS_IN_VIEWING_LIST);
			ArrayList<FileListItem> tmpList = savedInstanceState
					.getParcelableArrayList(ITEMS_MANAGER);
			itemManager = new ObjectCollection<FileListItem>(
					FileListItem.class, tmpList, 10);
			refreshView(sum, savedInstanceState.getString(UP_FOLDER_NAME));
		} else {
			sum = new ArrayList<FileListItem>();
			try {
				itemManager = new ObjectCollection<FileListItem>(
						FileListItem.class, 50, 10);
			} catch (InstantiationException e) {
				e.printStackTrace();
				throw new RuntimeException(e.toString());
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				throw new RuntimeException(e.toString());
			}
			if ((getIntent().getExtras() != null)
					&& (rootDir = getIntent().getExtras().getString(ROOT_DIR)) != null) {
			} else {
				rootDir = Environment.getExternalStorageDirectory().getPath();

			}
			try {
				setList(rootDir);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e.toString());
			} catch (InstantiationException e) {
				e.printStackTrace();
				throw new RuntimeException(e.toString());
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				throw new RuntimeException(e.toString());
			}
		}

		MyConfiguration.setDefaultConfiguration();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.file_explorer_context_menu, menu);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case MOVE:
			if (resultCode == Activity.RESULT_OK) {
				System.out.println("dosta resultCode="
						+ Integer.valueOf(requestCode));
				String newPath = data.getExtras().getString(
						SelectFileFolderActivity.RESULT);
				String oldName = data.getExtras().getString(SOURCE_FILE_PATH);
				if (newPath == null || oldName == null) {
					// TODO wrong data
				} else {
					File old = new File(oldName);
					File nev = new File(newPath, old.getName());
					if (!old.renameTo(nev)) {
						MyDialogs.Info.show(FileExplorerActivity.this, "Error",
								"Cannot move file to new destination",
								R.string.ok);
					} else {
						refreshList();
					}
				}
			} else {
				System.out.println("result failure");
			}
			break;
		case COPY_FOLDER:
			File source = new File(data.getExtras().getString(SOURCE_FILE_PATH));
			File newCopy = new File(new StringBuilder(data.getExtras()
					.getString(SelectFileFolderActivity.RESULT))
					.append(File.separator).append(source.getName()).toString());
			MyFiles.copyDir(source, newCopy, false);
			refreshList();
			break;
		case COPY_FILE:
			File src = new File(data.getExtras().getString(SOURCE_FILE_PATH));
			File dest = new File(new StringBuilder(data.getExtras().getString(
					SelectFileFolderActivity.RESULT)).append(File.separator)
					.append(src.getName()).toString());
			MyFiles.copyFile(src, dest, false);
			refreshList();
			break;
		default:

			break;
		}

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.move:
			Intent intent = new Intent();
			intent.setClass(this, SelectFolderActivity.class);
			intent.putExtra(SOURCE_FILE_PATH, ((FileListItem) getListAdapter()
					.getItem(info.position)).getPath());
			startActivityForResult(intent, MOVE);
			break;
		case R.id.del:
			final File del = new File(((FileListItem) getListAdapter().getItem(
					info.position)).getPath());
			MyDialogs.Confirm.show(
					FileExplorerActivity.this,
					getString(R.string.deleting_),
					new StringBuilder(
							getString(R.string.are_you_sure_to_delete)).append(
							del.getName()).toString(), R.string.yes,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							MyFiles.delete(del);
							refreshList();
						}
					}, R.string.no, null);
			break;
		case R.id.prop:
			// TODO zaimplementować okienko z właściwościami pliku
			System.out.println("Properties of file "
					+ getListAdapter().getItem(info.position));
			break;
		case R.id.rename:
			final File old = new File(((FileListItem) getListAdapter().getItem(
					info.position)).getPath());
			MyDialogs.Input.show(FileExplorerActivity.this,
					getString(R.string.renaming_),
					getString(R.string.type_new_name), R.string.ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							String value = MyDialogs.Input.getInput();
							if (value.equals("")) {
								return;
							}
							File nev = new File(currentPath, value);
							if (old.renameTo(nev)) {
								MyDialogs.Info
										.show(FileExplorerActivity.this,
												"Unable rename",
												new StringBuilder(
														getString(R.string.cannot_rename))
														.append(nev.getName())
														.toString(),
												R.string.ok);
							} else {
								refreshList();
							}
						}
					}, R.string.cancel, null);
			break;
		case R.id.copy:
			final File prev = new File(((FileListItem) getListAdapter()
					.getItem(info.position)).getPath());
			Intent i = new Intent();
			i.putExtra(SOURCE_FILE_PATH, ((FileListItem) getListAdapter()
					.getItem(info.position)).getPath());
			i.setClass(this, SelectFolderActivity.class);
			if (prev.isDirectory()) {
				startActivityForResult(i, COPY_FOLDER);
			} else {
				startActivityForResult(i, COPY_FILE);
			}
			break;
		default:
			System.out.println("unexpected option");
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.file_explorer_settings_menu, menu);
		menu.findItem(R.id.newfile).setOnMenuItemClickListener(this);
		menu.findItem(R.id.newdir).setOnMenuItemClickListener(this);
		return true;
	}

	@Override
	public boolean onMenuItemClick(final MenuItem item) {
		MyDialogs.Input.show(FileExplorerActivity.this, item.getTitle()
				.toString(), "Type name:", R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						String value = MyDialogs.Input.getInput();
						File f = new File(currentPath, value);
						System.out.print(item.getTitle());
						System.out.print(currentPath + " || " + value
								+ " effect: ");
						switch (item.getItemId()) {
						case R.id.newdir:
							if (!f.mkdir()) {
								MyDialogs.Info.show(FileExplorerActivity.this,
										"Unable create",
										"Cannot create folder", R.string.ok);
							}
							break;
						case R.id.newfile:
							try {
								if (!f.createNewFile()) {
									MyDialogs.Info.show(
											FileExplorerActivity.this,
											"Unable create",
											"Cannot create file", R.string.ok);
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
							break;
						}
						refreshList();
					}
				}, R.string.cancel, null);
		return true;
	}

	private void refreshList() {
		try {
			setList(currentPath);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	void setList(String path) throws IOException, InstantiationException,
			IllegalAccessException {
		File file = new File(path);

		fls.clear();
		dirs.clear();
		sum.clear();

		if (!file.isDirectory()) {
			throw new IllegalArgumentException("Path is not a directory!");
		}

		FileListItem item = itemManager.get();

		File[] files = file.getCanonicalFile().listFiles();
		for (int i = 0; i < files.length; ++i) {

			item = itemManager.get();
			item.setPath(files[i].getPath());

			// setting type
			MimeTypeMap mime = MimeTypeMap.getSingleton();
			String ext = files[i].getName().substring(
					files[i].getName().lastIndexOf(".") + 1);
			String type = mime.getMimeTypeFromExtension(ext);
			item.setLastModificationDate(type);

			// setting data field and image
			if (files[i].isDirectory()) {
				if ((files[i].isHidden() && PrivateSettings.showHidden)
						|| (!files[i].isHidden() && !PrivateSettings.showHidden)) {
					File[] f = files[i].listFiles();
					if (f != null) {
						item.setData(Integer.toString(f.length));
					} else {
						item.setData("IOException");
					}
					item.setImage(DIR_TYPE);
					item.setName(new StringBuilder().append(files[i].getName())
							.append("/").toString());
					dirs.add(item);
				}
			} else {
				item.setName(files[i].getName());
				item.setData(new StringBuilder().append(files[i].length())
						.append(" bytes").toString());
				item.setImage(FILE_TYPE);
				fls.add(item);
			}
		}

		Collections.sort(dirs);
		Collections.sort(fls);

		sum.addAll(dirs);
		sum.addAll(fls);
		itemManager.addAll(sum);
		currentPath = path;
		parentPath = path.equals(rootDir) ? rootDir : file.getParent();
		refreshView(
				sum,
				path.equals(rootDir) ? getString(R.string.do_not_go_up) : file
						.getName());
	}

	private void refreshView(ArrayList<FileListItem> list, String folderName) {
		// adapter.setItemsList(list);
		pathView.setText(folderName);
		setListAdapter(new FileArrayAdapter(this,
				R.layout.file_explorer_item_layout, list));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String curPath = sum.get(position).getPath();
		System.out.print("selected file: ");
		System.out.println(curPath);

		File selected = new File(curPath);
		if (selected.canRead()) {
			if (selected.isDirectory()) {
				try {
					setList(curPath);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			} else {
				MimeTypeMap mime = MimeTypeMap.getSingleton();
				String ext = selected.getName().substring(
						selected.getName().lastIndexOf(".") + 1);
				String type = mime.getMimeTypeFromExtension(ext);
				System.out.println(ext);
				try {
					Intent i = new Intent();
					i.setAction(Intent.ACTION_VIEW).setDataAndType(
							Uri.fromFile(selected), type);
					startActivity(i);
				} catch (ActivityNotFoundException e) {
					new AlertDialog.Builder(this)
							.setTitle(
									new StringBuilder().append("[")
											.append(curPath)
											.append("] dont know how open!")
											.toString())
							.setPositiveButton("OK", null).show();
				}
			}
		} else {
			new AlertDialog.Builder(this)
					.setTitle(
							new StringBuilder().append("[").append(curPath)
									.append("] folder can't be read!")
									.toString()).setPositiveButton("OK", null)
					.show();
		}
	}

	public void onUpClick(View v) {
		try {
			setList(parentPath);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArrayList(ITEMS_IN_VIEWING_LIST, sum);
		outState.putParcelableArrayList(ITEMS_MANAGER, itemManager.getList());
		outState.putString(ROOT_DIR, rootDir);
		outState.putString(PARENT_DIR, parentPath);
		outState.putString(UP_FOLDER_NAME, pathView.getText().toString());
		outState.putString(CURRENT_DIR, currentPath);
	}

}
