package com.example.app;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ValueStoreSQLite implements ValueStore {

	private Context context;
	private ValueDbHelper dbHelper;
  
  @Override
	public String getValue(String name) {
		if (dbHelper == null) {
			Log.e(TAG, "Cannot get value (database not available)");
			return null;
		}
		return dbHelper.selectValue(name);
	}

	@Override
	public void setValue(String name, String value) {
		if (dbHelper == null) {
			Log.e(TAG, "Cannot set value (database not available)");
			return;
		}
		dbHelper.updateOrInsertValue(name, value);
	}

	public ValueStoreSQLite(Context context) {
		this.context = context;
	}

  public synchronized void open() {
		if (dbHelper != null) {
			return;
		}
		dbHelper = new ValueDbHelper(context);
	}

	public synchronized void close() {
		dbHelper.close();
		dbHelper = null;
	}

	private static class ValueDbHelper extends SQLiteOpenHelper {

    private static final int DB_SCHEMA_VERSION_2 = 2;
		private static final int DB_VERSION = DB_SCHEMA_VERSION_2;

		private static final String DB = "valuestore";

    private static final String CREATE_VALUE_STORE_TABLE =
        "CREATE TABLE " + VALUE_STORE +
         "(" +
         COLUMN_KEY + " TEXT PRIMARY KEY," +
         COLUMN_VALUE + " TEXT" +
         ")";

    private SQLiteDatabase db;

    public ValueDbHelper(Context context) {
			super(context, DB, null, DB_VERSION);
			db = getWritableDatabase();
			db.execSQL("PRAGMA foreign_keys = ON;");
		}

    @Override
		public void onCreate(SQLiteDatabase db) {
      db.execSQL(CREATE_VALUE_STORE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades if needed
    }
    
		public String selectValue(String name) {
      String[] columns = {COLUMN_VALUE};
      String selection = COLUMN_KEY + "=?";
      String[] selectionArgs = { name };
      Cursor cursor = db.query(VALUE_STORE, columns, selection, selectionArgs, null, null, null);
      String value = null;
      if (cursor.moveToFirst()) {
        value = cursor.getString(cursor.getColumnIndex(COLUMN_VALUE));
      }
      cursor.close();
      return value;
		}

		public void updateOrInsertValue(String name, String value) {
      String selection = COLUMN_KEY + "=?";
      String[] selectionArgs = {name};
      ContentValues fields = new ContentValues();
      fields.put(COLUMN_KEY, key);
      fields.put(COLUMN_VALUE, value);
      Cursor cursor = db.query(VALUE_STORE, null, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
          // Key already exists, update the value
          db.update(VALUE_STORE, fields, selection, selectionArgs);
        } else {
          // Key doesn't exist, insert a new row
          db.insert(VALUE_STORE, null, fields);
        }
      cursor.close();      
		}
  }
}
