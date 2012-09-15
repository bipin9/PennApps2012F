package upenn.pennapps2012f;

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
			"eventEndTime long NOT NULL)";

	protected static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	db.execSQL(TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
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
		return this;
	}

	/**
	 * Close all associated database tables
	 */
	public void close() {
		Log.w(TAG, "Closing CourseSearchCache");
		mDbHelper.close();
	}
	
	public void initializeTestData() {
		mDb.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		mDb.execSQL(TABLE_CREATE);

		long curr = System.currentTimeMillis();
		
		ContentValues values = new ContentValues();
		values.put("eventName", "TEST1");
		values.put("eventStartTime", curr + 10000);
		values.put("eventEndTime", curr + 10000);
		if (mDb.insert(TABLE_NAME, null, values) == -1) 
			Log.w(TAG, "Failed to insert new course into table");
		

		values = new ContentValues();
		values.put("eventName", "TEST2");
		values.put("eventStartTime", curr + 20000);
		values.put("eventEndTime", curr + 20000);
		if (mDb.insert(TABLE_NAME, null, values) == -1) 
			Log.w(TAG, "Failed to insert new course into table");
		

		values = new ContentValues();
		values.put("eventName", "TEST3");
		values.put("eventStartTime", curr + 30000);
		values.put("eventEndTime", curr + 30000);
		if (mDb.insert(TABLE_NAME, null, values) == -1) 
			Log.w(TAG, "Failed to insert new course into table");
	}

	public void addEntry(EventEntry next) {
		ContentValues values = new ContentValues();
		values.put("eventName", next.eventName);
		values.put("eventStartTime", next.eventStartTime);
		values.put("eventEndTime", next.eventEndTime);
		if (mDb.insert(TABLE_NAME, null, values) == -1) 
			Log.w(TAG, "Failed to insert new course into table");
		
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
}
