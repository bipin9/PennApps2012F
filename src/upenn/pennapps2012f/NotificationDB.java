package upenn.pennapps2012f;

import upenn.pennapps2012f.EventsDB.DatabaseHelper;
import android.content.Context;
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
			"notificationMessage char(500) NOT NULL," +
			"notificationTime long NOT NULL," + 
			"notificationSender char(100) NOT NULL)";
	
	private final static String OCCURRED_EVENTS_TABLE = "Occurred_Events_Table";
	private final static String OCCURRED_EVENTS_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + OCCURRED_EVENTS_TABLE + " (" +
			"eventName char(50) NOT NULL," +
			"eventStartTime long NOT NULL," +
			"eventEndTime long NOT NULL)";
	
	private final static String 
	
	protected static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	db.execSQL(NOTIFICATION_TABLE_CREATE);
        	db.execSQL(OCCURRED_EVENTS_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + NOTIFICATION_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + OCCURRED_EVENTS_TABLE);
            onCreate(db);
        }
    }
}
