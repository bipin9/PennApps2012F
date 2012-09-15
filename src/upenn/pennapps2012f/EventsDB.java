package upenn.pennapps2012f;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class EventsDB {
	
	private Context mCtx;

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	private final static String TAG = "EVENTS_DB";
	private final static String DATABASE_NAME = "GCAL_FUTURE_EVENTS";
	private final static int DATABASE_VERSION = 1;
	
	private final static String TABLE_NAME = "Future_Events_Table";
	private final static String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + 
			"eventId integer PRIMARY KEY AUTOINCREMENT," +
			"eventName char(50) NOT NULL," +
			"eventStartTime long NOT NULL," +
			"eventEndTime long NOT NULL," +
			"UNIQUE(eventName, eventStartTime, eventEndTime) ON CONFLICT IGNORE)";
	
	private final static String GLOBAL_VAR_TABLE_NAME = "GLOBAL_VARS";
	private final static String GLOBA_VAR_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + GLOBAL_VAR_TABLE_NAME + " (" +
			"eventId integer PRIMARY KEY AUTOINCREMENT," +
			"eventName char(50) NOT NULL," +
			"eventStartTime long NOT NULL," +
			"eventEndTime long NOT NULL)";

	protected static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	db.execSQL(TABLE_CREATE);
        	db.execSQL(GLOBA_VAR_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + GLOBAL_VAR_TABLE_NAME);
            onCreate(db);
        }
    }
	
	public EventsDB(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * Open the SQLite database and get the associating tables (if they exist, else create them)
	 * @return SearchCache with the database opened
	 * @throws SQLException
	 */
	public EventsDB open() throws SQLException {
		Log.w(TAG, "Opening CourseSearchCache");
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		mDb.execSQL(TABLE_CREATE);
		mDb.execSQL(GLOBA_VAR_TABLE_CREATE);
		return this;
	}

	/**
	 * Close all associated database tables
	 */
	public void close() {
		Log.w(TAG, "Closing CourseSearchCache");
		mDbHelper.close();
	}
	
	// DEBUG ONLY
//	public void initializeTestData() {
//		mDb.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
//		mDb.execSQL(TABLE_CREATE);
//
//		long curr = System.currentTimeMillis();
//		
//		ContentValues values = new ContentValues();
//		values.put("eventName", "TEST1");
//		values.put("eventStartTime", curr + 3000);
//		values.put("eventEndTime", curr + 6000);
//		if (mDb.insert(TABLE_NAME, null, values) == -1) 
//			Log.w(TAG, "Failed to insert new course into table");
//		
//
//		values = new ContentValues();
//		values.put("eventName", "TEST2");
//		values.put("eventStartTime", curr + 6000);
//		values.put("eventEndTime", curr + 10000);
//		if (mDb.insert(TABLE_NAME, null, values) == -1) 
//			Log.w(TAG, "Failed to insert new course into table");
//
//		values = new ContentValues();
//		values.put("eventName", "TEST3");
//		values.put("eventStartTime", curr + 10000);
//		values.put("eventEndTime", curr + 14000);
//		if (mDb.insert(TABLE_NAME, null, values) == -1) 
//			Log.w(TAG, "Failed to insert new course into table");
//	}
	
	public void updateEntry(EventEntry[] newEntries) {
		if (newEntries != null) {
			for (int i = 0; i < newEntries.length; i++) {
				addEntry(newEntries[i]);
			}
		}
	}

	private void addEntry(EventEntry next) {
		System.out.println("Adding event " + ((next.eventName == null) ? "null" : next.eventName) + " with beginning time " +
				new Date(next.eventStartTime).toString() + " and end time " + new Date(next.eventEndTime).toString());
		
		ContentValues values = new ContentValues();
		values.put("eventName", next.eventName);
		values.put("eventStartTime", next.eventStartTime);
		values.put("eventEndTime", next.eventEndTime);
		mDb.insert(TABLE_NAME, null, values);
	}
	
	public EventEntry getNextEntry() {
		// Remove any events that are passed the current time already
		Cursor c = mDb.rawQuery("SELECT eventId FROM " + TABLE_NAME + " WHERE eventEndTime<" + System.currentTimeMillis(), null);
		c.moveToFirst();
		while (c.getCount() > 0) {
			do {
				int id = c.getInt(c.getColumnIndex("eventId"));
				mDb.delete(TABLE_NAME, "eventId=" + id, null);
			} while (c.moveToNext());
		}
		
		// Get the most recent time
		c = mDb.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY eventStartTime LIMIT 1", null);
		c.moveToFirst();
		if (c.getCount() == 1) {
			// Get the most recent entry
			EventEntry entry = new EventEntry();
			entry.id = c.getInt(c.getColumnIndex("eventId"));
			entry.eventName = c.getString(c.getColumnIndex("eventName"));
			entry.eventStartTime = c.getLong(c.getColumnIndex("eventStartTime"));
			entry.eventEndTime = c.getLong(c.getColumnIndex("eventEndTime"));
			
			// Remove that entry
			mDb.delete(TABLE_NAME, "eventId=" + entry.id, null);
			
			return entry;
		}
		return null;
	}
	
	public void setCurrentEntry(EventEntry entry) {
		mDb.delete(GLOBAL_VAR_TABLE_NAME, null, null);
		
		System.out.println("Setting current entry to " + ((entry == null) ? "NULL" : entry.eventName));
		
		if (entry != null) {
			ContentValues values = new ContentValues();
			values.put("eventId", entry.id);
			values.put("eventName", entry.eventName);
			values.put("eventStartTime", entry.eventStartTime);
			values.put("eventEndTime", entry.eventEndTime);
			
			mDb.insert(GLOBAL_VAR_TABLE_NAME, null, values);
		}
	}
	
	public EventEntry getCurrentEntry() {
		Cursor c = mDb.rawQuery("SELECT * FROM " + GLOBAL_VAR_TABLE_NAME, null);
		c.moveToFirst();
		
		if (c.getCount() == 1) {
			EventEntry entry = new EventEntry();
			entry.id = c.getInt(c.getColumnIndex("eventId"));
			entry.eventName = c.getString(c.getColumnIndex("eventName"));
			entry.eventStartTime = c.getLong(c.getColumnIndex("eventStartTime"));
			entry.eventEndTime = c.getLong(c.getColumnIndex("eventEndTime"));
			
			System.out.println("Getting current entry: " + entry.eventName);
			
			return entry;
		}
		System.out.println("Getting current entry: NULL");
		return null;
	}
}
