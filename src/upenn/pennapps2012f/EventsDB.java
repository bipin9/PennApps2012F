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
	 * @return 
	 * @throws SQLException
	 */
	public EventsDB open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		
		// Might be unnecessary, just in case the tables aren't instantiated
		mDb.execSQL(TABLE_CREATE);
		mDb.execSQL(GLOBA_VAR_TABLE_CREATE);
		
		return this;
	}

	/**
	 * Close all associated database tables
	 */
	public void close() {
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
//		values.put("eventStartTime", curr + 1000);
//		values.put("eventEndTime", curr + 10000);
//		if (mDb.insert(TABLE_NAME, null, values) == -1) 
//			Log.w(TAG, "Failed to insert new course into table");
//		
//
//		values = new ContentValues();
//		values.put("eventName", "TEST2");
//		values.put("eventStartTime", curr + 15000);
//		values.put("eventEndTime", curr + 20000);
//		if (mDb.insert(TABLE_NAME, null, values) == -1) 
//			Log.w(TAG, "Failed to insert new course into table");
//
//		values = new ContentValues();
//		values.put("eventName", "TEST3");
//		values.put("eventStartTime", curr + 21000);
//		values.put("eventEndTime", curr + 100000);
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
		mDb.delete(TABLE_NAME, "eventEndTime<" + System.currentTimeMillis(), null);
		
		// Get the most recent time
		Cursor c = mDb.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY eventStartTime LIMIT 1", null);
		c.moveToFirst();
		if (c.getCount() > 0) {
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
		// Remove any events that are passed the current time already
		mDb.delete(GLOBAL_VAR_TABLE_NAME, "eventEndTime<" + System.currentTimeMillis(), null);
		
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
