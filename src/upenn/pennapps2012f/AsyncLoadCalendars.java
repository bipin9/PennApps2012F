package upenn.pennapps2012f;

import java.util.Date;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.text.format.Time;

public class AsyncLoadCalendars extends AsyncTask<ContentResolver, Void, Void> {
	private Cursor mCursor = null;
	private static final String[] COLS = new String[] { CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND};
	private Context context;
	
	public AsyncLoadCalendars(Context mContext) {
		context = mContext;
	}
	
	@Override
	protected Void doInBackground(ContentResolver... args) {
		String selection = "((" + CalendarContract.Events.DTSTART + " <= ?) AND (" + 
				CalendarContract.Events.DTEND + " >= ?) AND (" + 
				CalendarContract.Events.ALL_DAY + " = 0))";
		Time t = new Time();
		t.setToNow();
		String dtStart = Long.toString(t.toMillis(false));
		t.set(59, 59, 23, t.monthDay, t.month, t.year);
		String dtEnd = Long.toString(t.toMillis(false));
		String[] selectionArgs = new String[] { dtEnd, dtStart};
		
		mCursor = args[0].query(CalendarContract.Events.CONTENT_URI, COLS, selection, selectionArgs, CalendarContract.Events.DTSTART);
			
		mCursor.moveToFirst();
		EventEntry[] events = new EventEntry[mCursor.getCount()];
		int count = 0;
		while(!mCursor.isAfterLast())
		{
			String name = mCursor.getString(0);
			long start = mCursor.getLong(1);
			long end = mCursor.getLong(2);
			events[count++] = new EventEntry(name, start, end);
			mCursor.moveToNext();
		}
	  
		System.out.println("Done with loop, count is " + mCursor.getCount());
		
		EventsDB db = new EventsDB(context);
		db.open();
		db.updateEntry(events);
		db.close();
		
		return null;
	}

	protected void onPostExecute(Void result) {
		if (!Alarm.Running) {
			new Alarm().setAlarm(context);
		}
	}
}