package upenn.pennapps2012f;

import java.text.Format;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.format.DateFormat;
import android.text.format.Time;

class AsyncLoadCalendars extends AsyncTask<ContentResolver, Void, Void> {
	private Cursor mCursor = null;
	private static final String[] COLS = new String[] { CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND};
	
	
  @Override
  protected Void doInBackground(ContentResolver... args) {
	  String selection = "((" + CalendarContract.Events.DTSTART
	            + " <= ?) AND (" + CalendarContract.Events.DTEND + " >= ?))";
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
	  }
	  
	  
	  return null;
  }

  
}