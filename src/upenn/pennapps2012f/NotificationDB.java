package upenn.pennapps2012f;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NotificationDB {

	private Context mCtx;

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	private final static String TAG = "NOTIFICATION_DB";
	private final static String DATABASE_NAME = "GCAL_FUTURE_EVENTS";
	private final static int DATABASE_VERSION = 1;
	
	private final static String NOTIFICATION_TABLE = "Notification_Table";
	private final static String NOTIFICATION_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + NOTIFICATION_TABLE + " (" +
			"notificationId integer PRIMARY KEY AUTOINCREMENT," +
			"notificationType integer NOT NULL," +
			"notificationSubject char(300) NOT NULL," +
			"notificationMessage char(500)," +
			"notificationTime long NOT NULL," + 
			"notificationSender char(100) NOT NULL," +
			"UNIQUE(notificationType, notificationSubject, notificationMessage, notificationTime, notificationSender) ON CONFLICT IGNORE)";
	
	private final static String OCCURRED_EVENTS_TABLE = "Occurred_Events_Table";
	private final static String OCCURRED_EVENTS_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + OCCURRED_EVENTS_TABLE + " (" +
			"eventName char(50) NOT NULL," +
			"eventStartTime long NOT NULL," +
			"eventEndTime long NOT NULL)";
	
	// For Demo purposes
	private final static String AUTHENTICATION_TABLE = "Authentication_Table";
	private final static String AUTHENTICATION_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + AUTHENTICATION_TABLE + " (" +
			"username char(100) NOT NULL," +
			"password char(100) NOT NULL)";
	
	protected static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	db.execSQL(NOTIFICATION_TABLE_CREATE);
        	db.execSQL(OCCURRED_EVENTS_TABLE_CREATE);
        	db.execSQL(AUTHENTICATION_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + NOTIFICATION_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + OCCURRED_EVENTS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + AUTHENTICATION_TABLE);
            onCreate(db);
        }
    }
	
	public NotificationDB(Context ctx) {
		this.mCtx = ctx;
	}
	
	/**
	 * Open the SQLite database and get the associating tables (if they exist, else create them)
	 * @return
	 * @throws SQLException
	 */
	public NotificationDB open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		
		// Might be unnecessary, just in case the tables aren't instantiated
    	mDb.execSQL(NOTIFICATION_TABLE_CREATE);
    	mDb.execSQL(OCCURRED_EVENTS_TABLE_CREATE);
    	mDb.execSQL(AUTHENTICATION_TABLE_CREATE);
		
		return this;
	}

	/**
	 * Close all associated database tables
	 */
	public void close() {
		mDbHelper.close();
	}
	
	public void addAuthentication(String name, String pw) {
		ContentValues values = new ContentValues();
		values.put("username", name);
		values.put("password", pw);
		
		mDb.insert(AUTHENTICATION_TABLE, null, values);
	}
	
	// return[0] is name, return[1] is pw
	public String[] getAuthentication() {
		Cursor c = mDb.rawQuery("SELECT * FROM " + AUTHENTICATION_TABLE, null);
		c.moveToFirst();
		
		if (c.getCount() > 0) {
			String[] result = new String[2];
			result[0] = c.getString(c.getColumnIndex("username"));
			result[1] = c.getString(c.getColumnIndex("password"));
			return result;
		}
		return null;
	}

	public void addOccurredEvent(EventEntry entry) {
		ContentValues values = new ContentValues();
		values.put("eventName", entry.eventName);
		values.put("eventStartTime", entry.eventStartTime);
		values.put("eventEndTime", entry.eventEndTime);
		
		mDb.insert(OCCURRED_EVENTS_TABLE, null, values);
	}
	
	public EventEntry[] getOccurredEvents() {
		Cursor c = mDb.rawQuery("SELECT * FROM " + OCCURRED_EVENTS_TABLE, null);
		c.moveToFirst();
		if (c.getCount() > 0) {
			EventEntry[] result = new EventEntry[c.getCount()];
			int index = 0;
			do {
				EventEntry entry = new EventEntry();
				entry.eventName = c.getString(c.getColumnIndex("eventName"));
				entry.eventStartTime = c.getLong(c.getColumnIndex("eventStartTime"));
				entry.eventEndTime = c.getLong(c.getColumnIndex("eventEndTime"));
				
				result[index++] = entry;
			} while (c.moveToNext());
			
			return result;
		}
		return null;
	}
	
	public void resetOccurredEvents() {
		mDb.delete(OCCURRED_EVENTS_TABLE, null, null);
	}

	public void addNotification(Notification[] n) {
		for (int i = 0; i < n.length; i++) {
			addNotification(n[i]);
		}
	}
	
	public void addNotification(Notification n) {
		System.out.println("Adding notification - name: " + n.subject + " sender: " + n.sender);
		
		ContentValues values = new ContentValues();
		values.put("notificationSubject", n.subject);
		values.put("notificationType", n.type);
		values.put("notificationMessage", n.message);
		values.put("notificationTime", n.time);
		values.put("notificationSender", n.sender);
		
		mDb.insert(NOTIFICATION_TABLE, null, values);
	}
	
	public Notification[] getAllNotifications() {
		Cursor c = mDb.rawQuery("SELECT * FROM " + NOTIFICATION_TABLE + " ORDER BY notificationTime", null);
		c.moveToFirst();
		if (c.getCount() > 0) {
			Notification[] result = new Notification[c.getCount()];
			int index = 0;
			do {
				Notification n = new Notification();
				n.id = c.getInt(c.getColumnIndex("notificationId"));
				n.type = c.getInt(c.getColumnIndex("notificationType"));
				n.subject = c.getString(c.getColumnIndex("notificationSubject"));
				n.message = c.getString(c.getColumnIndex("notificationMessage"));
				n.time = c.getLong(c.getColumnIndex("notificationTime"));
				n.sender = c.getString(c.getColumnIndex("notificationSender"));
				
				result[index++] = n;
			} while (c.moveToNext());
			
			return result;
		}
		return null;
	}
	
	public void deleteNotification(Notification n) {
		
	}
	
}
