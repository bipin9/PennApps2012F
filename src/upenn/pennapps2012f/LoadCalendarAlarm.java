package upenn.pennapps2012f;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LoadCalendarAlarm extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// Load asyncload
		new AsyncLoadCalendars(context).execute(context.getContentResolver());
		setAlarm(context);
	}
	
	public void setAlarm(Context context) {
		cancelAlarm(context);
		
		AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, LoadCalendarAlarm.class);
	    
	    PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
	    am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000 * 60 * 60 * 6), pi);		// set for 6 hours later
	}
	
	public void cancelAlarm(Context context) {
	    Intent intent = new Intent(context, LoadCalendarAlarm.class);
	    PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
	    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	    alarmManager.cancel(sender);
	    sender.cancel();
	} 

}
