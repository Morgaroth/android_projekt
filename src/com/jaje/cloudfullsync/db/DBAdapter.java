package com.jaje.cloudfullsync.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jaje.cloudfullsync.CloudDrive;
import com.jaje.cloudfullsync.binding.Binding;
import com.jaje.cloudfullsync.binding.Bindings;
import com.jaje.cloudfullsync.binding.SyncDirect;

public class DBAdapter {

	private static final String DB_NAME = "database.db";
	private static final String DB_TABLE = "Bindings";

	private static final int DB_VERSION = 4;

	public static final String KEY_ID = "no";
	public static final String ID_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT";

	public static final String KEY_SOURCE = "source";
	public static final String SOURCE_OPTION = "TEXT NOT NULL";
	public static final String KEY_LOCALPATH = "local_path";
	public static final String LOCALPATH_OPTION = "TEXT NOT NULL";
	public static final String KEY_CLOUDPATH = "cloud_path";
	public static final String CLOUDPATH_OPTION = "TEXT NOT NULL";
	public static final String KEY_DIRECTION = "direction";
	public static final String DIRECTION_OPTION = "TEXT NOT NULL";
	public static final String KEY_ACTIVE = "active";
	public static final String ACTIVE_OPTION = "INTEGER NOT NULL";
	public static final String KEY_FREQUENCY = "frequency";
	public static final String FREQUENCY_OPTION = "TEXT NOT NULL";

	private static final String DB_CREATE = "create table " + DB_TABLE + " ("
			+ KEY_ID + " " + ID_OPTIONS + ", " + KEY_SOURCE + " "
			+ SOURCE_OPTION + ", " + KEY_LOCALPATH + " " + LOCALPATH_OPTION
			+ ", " + KEY_CLOUDPATH + " " + CLOUDPATH_OPTION + ", "
			+ KEY_DIRECTION + " " + DIRECTION_OPTION + ", " + KEY_ACTIVE + " "
			+ ACTIVE_OPTION + ", " + KEY_FREQUENCY + " " + FREQUENCY_OPTION
			+ ");";

	// Zmienna do przechowywania bazy danych
	private SQLiteDatabase db;

	// Kontekst aplikacji korzystającej z bazy
	private final Context context;

	// Helper od otwierania i aktualizacji bazy danych
	private DatabaseHelper myDatabaseHelper;

	// C-tor
	public DBAdapter(Context context) {
		this.context = context;
		myDatabaseHelper = new DatabaseHelper(this.context, DB_NAME, null,
				DB_VERSION);
	}

	// Otwieramy połączenie z bazą danych
	public DBAdapter open() {
		db = myDatabaseHelper.getWritableDatabase();
		return this;
	}

	public long insertBinding(Binding bind) {
		ContentValues record = new ContentValues();
		record.put(KEY_SOURCE, bind.getSource().name());
		record.put(KEY_LOCALPATH, bind.localPath);
		record.put(KEY_CLOUDPATH, bind.cloudPath);
		record.put(KEY_DIRECTION, bind.getDirect().name());
		record.put(KEY_ACTIVE, bind.isActive() ? 1 : 0);
		record.put(KEY_FREQUENCY, bind.getFrequency());
		return db.insert(DB_TABLE, null, record);
	}

	public DBAdapter saveBindings(Bindings bindings) {

		db.delete(DB_TABLE, null, null);
		for (Binding b : bindings.bindingsList) {
			insertBinding(b);
		}
		return this;
	}

	public DBAdapter readBindingsInto(Bindings bindings) {
		Cursor cursor = getAllEntries();
		int sourceIndex = cursor.getColumnIndex(KEY_SOURCE);
		int localPathIndex = cursor.getColumnIndex(KEY_LOCALPATH);
		int cloudPathIndex = cursor.getColumnIndex(KEY_CLOUDPATH);
		int directionIndex = cursor.getColumnIndex(KEY_DIRECTION);
		int activeIndex = cursor.getColumnIndex(KEY_ACTIVE);
		int frequencyIndex = cursor.getColumnIndex(KEY_FREQUENCY);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			bindings.add(CloudDrive.valueOf(cursor.getString(sourceIndex)),
					cursor.getString(localPathIndex),
					cursor.getString(cloudPathIndex),
					SyncDirect.valueOf(cursor.getString(directionIndex)),
					cursor.getInt(activeIndex) == 1,
					cursor.getString(frequencyIndex));
			cursor.moveToNext();
		}
		return this;
	}

	public Cursor getAllEntries() {
		String[] columns = { KEY_SOURCE, KEY_LOCALPATH, KEY_CLOUDPATH,
				KEY_DIRECTION,KEY_ACTIVE,KEY_FREQUENCY };
		return db.query(DB_TABLE, columns, null, null, null, null, null);
	}

	// Zamykamy połączenie z bazą danych
	public void close() {
		db.close();
	}

	// Klasa helpera do otwierania i aktualizacji bazy danych
	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase _db) {
			_db.execSQL(DB_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase _db, int oldVer, int newVer) {
			_db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
			onCreate(_db);

			Log.w("ListView DatabaseAdapter", "Aktualizacja bazy z wersji "
					+ oldVer + " do " + newVer
					+ ". Wszystkie dane zostaną utracone.");
		}
	}
}
