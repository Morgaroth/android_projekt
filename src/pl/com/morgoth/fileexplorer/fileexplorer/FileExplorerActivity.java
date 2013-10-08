package pl.com.morgoth.fileexplorer.fileexplorer;

import jaje.android.lib.dialogs.MyDialogs;
import jaje.android.lib.files.MyFiles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import pl.com.morgoth.fileexplorer.R;
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

public class FileExplorerActivity extends ListActivity implements MenuItem.OnMenuItemClickListener {

	private static final int MOVE = 1;
	private static final int COPY_FOLDER = 2;
	private static final int COPY_FILE = 3;
	private static final String SOURCE_FILE_PATH = "sourcefile";
	private static final String FILE_TYPE = "file";
	private static final String DIR_TYPE = "dir";
	private static final String ROOT_DIR = "root";
	private static final String CURRENT_DIR = "current";

	private TextView pathView;

	private String rootPath;
	private String currentPath;
	private final ArrayList<FileListItem> sum = new ArrayList<FileListItem>();

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.file_explorer_context_menu, menu);
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
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(ROOT_DIR, rootPath);
		outState.putString(CURRENT_DIR, currentPath);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_explorer_layout);

		registerForContextMenu(getListView());

		prepareActivityHeader();
		setVariables(savedInstanceState);

	}

	private void prepareActivityHeader() {
		((TextView) findViewById(R.id.up_name)).setText("UP");
		ImageView imageCity = (ImageView) findViewById(R.id.up_img);
		int imageResource = getResources().getIdentifier("drawable/up", null, getPackageName());

		Drawable image = getResources().getDrawable(imageResource);
		imageCity.setImageDrawable(image);
		pathView = (TextView) findViewById(R.id.up_folder);
	}

	private void setVariables(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			setVariablesFromBundle(savedInstanceState);
		} else {
			setVariablesDefault();
		}
		tryFillFilesListFromCurrentPath();
	}

	private void setVariablesFromBundle(Bundle savedInstanceState) {
		rootPath = savedInstanceState.getString(ROOT_DIR);
		currentPath = savedInstanceState.getString(CURRENT_DIR);
	}

	private void setVariablesDefault() {
		rootPath = Environment.getExternalStorageDirectory().getPath();
		currentPath = rootPath;
	}

	private void tryFillFilesListFromCurrentPath() {
		try {
			fillFilesListFromPath(currentPath);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e.toString());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException(e.toString());
		}
	}

	private void fillFilesListFromPath(String path) throws IllegalAccessException, IOException {

		File file = new File(path);

		if (!file.isDirectory()) {
			throw new IllegalArgumentException("Path is not a directory!");
		}

		sum.clear();

		File[] files = file.getCanonicalFile().listFiles();

		generateFileItemsList(files);

		refreshView(sum, path.equals(rootPath) ? getString(R.string.do_not_go_up) : file.getName());
	}

	private void generateFileItemsList(File[] files) {

		ArrayList<FileListItem> dirs = new ArrayList<FileListItem>();
		ArrayList<FileListItem> fls = new ArrayList<FileListItem>();

		FileListItem item;
		for (int i = 0; i < files.length; ++i) {

			item = new FileListItem();
			item.setPath(files[i].getPath());

			setFileTypeInItem(files[i], item);

			// setting data field and image
			File filesfgd = files[i];
			if (filesfgd.isDirectory()) {
				if ((filesfgd.isHidden() && PrivateSettings.showHidden)
						|| (!filesfgd.isHidden() && !PrivateSettings.showHidden)) {
					File[] f = filesfgd.listFiles();
					if (f != null) {
						item.setData(Integer.toString(f.length));
					} else {
						item.setData("IOException");
					}
					item.setImage(DIR_TYPE);
					item.setName(new StringBuilder().append(filesfgd.getName()).append("/")
							.toString());
					dirs.add(item);
				}
			} else {
				item.setName(filesfgd.getName());
				item.setData(new StringBuilder().append(filesfgd.length()).append(" bytes")
						.toString());
				item.setImage(FILE_TYPE);
				fls.add(item);
			}
		}

		Collections.sort(dirs);
		Collections.sort(fls);

		sum.addAll(dirs);
		sum.addAll(fls);
	}

	private void setFileTypeInItem(File file, FileListItem item) {
		MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
		String fileExtension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
		String fileType = mimeTypeMap.getMimeTypeFromExtension(fileExtension);
		item.setLastModificationDate(fileType);
	}

	private void refreshView(ArrayList<FileListItem> list, String folderName) {
		pathView.setText(folderName);
		setListAdapter(new FileArrayAdapter(this, R.layout.file_explorer_item_layout, list));
	}

	public void onUpClick(View v) {
		currentPath = getParentPath();
		tryFillFilesListFromCurrentPath();
	}

	private String getParentPath() {
		if (!currentPath.equals(rootPath)) {
			currentPath = new File(currentPath).getParent();
		}
		return currentPath;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		File selected = new File(sum.get(position).getPath());

		if (selected.canRead()) {
			if (selected.isDirectory()) {
				currentPath = selected.getPath();
				tryFillFilesListFromCurrentPath();
			} else {
				openSelectedFile(selected);
			}
		} else {
			new AlertDialog.Builder(this)
					.setTitle(
							new StringBuilder().append("[").append(selected.getName())
									.append("] folder can't be read!").toString())
					.setPositiveButton("OK", null).show();
		}
	}

	private void openSelectedFile(File selected) {
		MimeTypeMap mime = MimeTypeMap.getSingleton();
		String ext = selected.getName().substring(selected.getName().lastIndexOf(".") + 1);
		String type = mime.getMimeTypeFromExtension(ext);

		try {
			Intent i = new Intent();
			i.setAction(Intent.ACTION_VIEW).setDataAndType(Uri.fromFile(selected), type);
			startActivity(i);
		} catch (ActivityNotFoundException e) {
			new AlertDialog.Builder(this)
					.setTitle(
							new StringBuilder().append("[").append(selected.getName())
									.append("] dont know how open!").toString())
					.setPositiveButton("OK", null).show();
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		final File selectedFile = new File(
				((FileListItem) getListAdapter().getItem(info.position)).getPath());

		switch (item.getItemId()) {

		case R.id.move:
			startMovingFileActivity(selectedFile);
			break;

		case R.id.del:
			deleteFileAfterConfirmation(selectedFile);
			break;

		case R.id.prop:
			showFilePropertiesDialog(selectedFile);
			break;

		case R.id.rename:
			renameFile(selectedFile);
			break;

		case R.id.copy:
			startCopyFileFolderActivity(selectedFile);
			break;

		default:
			System.out.println("unexpected option");
		}
		return true;
	}

	private void showFilePropertiesDialog(File file) {
		// TODO zaimplementować okienko z właściwościami pliku
		MyDialogs.Info.show(FileExplorerActivity.this, "Properties",
				"Properies dialog is under creating", R.string.ok);
	}

	private void deleteFileAfterConfirmation(final File fileToDelete) {
		MyDialogs.Confirm.show(
				FileExplorerActivity.this,
				getString(R.string.deleting_),
				new StringBuilder(getString(R.string.are_you_sure_to_delete)).append(
						fileToDelete.getName()).toString(), R.string.yes,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						MyFiles.delete(fileToDelete);
						tryFillFilesListFromCurrentPath();
					}
				}, R.string.no, null);
	}

	private void renameFile(final File fileToRename) {
		MyDialogs.Input.show(FileExplorerActivity.this, getString(R.string.renaming_),
				getString(R.string.type_new_name), R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						String newNameFromInput = MyDialogs.Input.getInput();
						if (newNameFromInput.equals("")) {
							return;
						}
						File newFile = new File(currentPath, newNameFromInput);
						if (fileToRename.renameTo(newFile)) {
							MyDialogs.Info.show(
									FileExplorerActivity.this,
									"Unable rename",
									new StringBuilder(getString(R.string.cannot_rename)).append(
											newFile.getName()).toString(), R.string.ok);
						} else {
							tryFillFilesListFromCurrentPath();
						}
					}
				}, R.string.cancel, null);
	}

	private void startCopyFileFolderActivity(final File selectedFile) {
		Intent copyIntent = new Intent();
		copyIntent.putExtra(SOURCE_FILE_PATH, selectedFile.getPath());
		copyIntent.setClass(this, SelectFolderActivity.class);
		if (selectedFile.isDirectory()) {
			startActivityForResult(copyIntent, COPY_FOLDER);
		} else {
			startActivityForResult(copyIntent, COPY_FILE);
		}
	}

	private void startMovingFileActivity(final File selectedFile) {
		Intent intent = new Intent();
		intent.setClass(this, SelectFolderActivity.class);
		intent.putExtra(SOURCE_FILE_PATH, selectedFile.getPath());
		startActivityForResult(intent, MOVE);
	}

	@Override
	public boolean onMenuItemClick(final MenuItem item) {
		MyDialogs.Input.show(FileExplorerActivity.this, item.getTitle().toString(), "Type name:",
				R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						String newFileName = MyDialogs.Input.getInput();
						File newFile = new File(currentPath, newFileName);
						switch (item.getItemId()) {
						case R.id.newdir:
							if (!newFile.mkdir()) {
								MyDialogs.Info.show(FileExplorerActivity.this, "Unable create",
										"Cannot create folder", R.string.ok);
							}
							break;
						case R.id.newfile:
							try {
								if (!newFile.createNewFile()) {
									MyDialogs.Info.show(FileExplorerActivity.this, "Unable create",
											"Cannot create file", R.string.ok);
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
							break;
						}
						tryFillFilesListFromCurrentPath();
					}
				}, R.string.cancel, null);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case MOVE:
			if (resultCode == Activity.RESULT_OK) {
				moveFileUsingDataFromIntent(data);
				tryFillFilesListFromCurrentPath();
			} else {
				System.out.println("result failure");
			}
			break;
		case COPY_FOLDER:
			copyFolderUsingDataFromIntent(data);
			tryFillFilesListFromCurrentPath();
			break;
		case COPY_FILE:
			copyFileUsingDataFromIntent(data);
			tryFillFilesListFromCurrentPath();
			break;
		default:
			break;
		}

	}

	private void copyFileUsingDataFromIntent(Intent intent) {
		File src = new File(intent.getExtras().getString(SOURCE_FILE_PATH));
		File dest = new File(new StringBuilder(intent.getExtras().getString(
				SelectFileFolderActivity.RESULT)).append(File.separator).append(src.getName())
				.toString());
		MyFiles.copyFile(src, dest, false);
	}

	private void copyFolderUsingDataFromIntent(Intent intent) {
		File source = new File(intent.getExtras().getString(SOURCE_FILE_PATH));
		File destination = new File(new StringBuilder(intent.getExtras().getString(
				SelectFileFolderActivity.RESULT)).append(File.separator).append(source.getName())
				.toString());
		MyFiles.copyDir(source, destination, false);
	}

	private void moveFileUsingDataFromIntent(Intent intent) {
		String newPath = intent.getExtras().getString(SelectFileFolderActivity.RESULT);
		String oldPath = intent.getExtras().getString(SOURCE_FILE_PATH);
		if (newPath == null || oldPath == null) {
			// TODO wrong data
		} else {
			File oldFile = new File(oldPath);
			File newFile = new File(newPath, oldFile.getName());
			if (!oldFile.renameTo(newFile)) {
				MyDialogs.Info.show(FileExplorerActivity.this, "Error",
						"Cannot move file to new destination", R.string.ok);
			}
		}
	}

}
