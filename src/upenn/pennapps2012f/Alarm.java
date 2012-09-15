package upenn.pennapps2012f;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PowerManager;

public class Alarm extends BroadcastReceiver {    
	
	@Override
	public void onReceive(Context context, Intent intent) {   
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        System.out.println("ALARM HAPPENED");
        Bundle extras = intent.getExtras();
        EventEntry entry = new EventEntry(extras.getString("EventData"));
        boolean started = extras.getBoolean("EventStarted");
        
        if (started) {
        	// set timer for end time
        	setAlarmHelper(context, entry, true);
        }
        else {
        	// reset for next event
        	((AudioManager)context.getSystemService(Context.AUDIO_SERVICE)).setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        	setAlarm(context);
        }
        
        wl.release();
    }

    public void setAlarm(Context context) {    	
	    // Get the next time
	    EventsDB db = new EventsDB(context);
	    db.open();
	    EventEntry entry = db.getNextEntry();
	    db.close(); 

	    setAlarmHelper(context, entry, false);
	}
    
    private void setAlarmHelper(Context context, EventEntry entry, boolean useStart) {
    	if (entry != null) {
	    	AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		    Intent i = new Intent(context, Alarm.class);
		    i.putExtra("EventData", entry.serialize());
		    i.putExtra("EventStarted", useStart);
		    
		    PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
	     
		    System.out.println("SETTING ALARM FOR " + entry.eventName);
		    
		    if (useStart) {
			    ((AudioManager)context.getSystemService(Context.AUDIO_SERVICE)).setRingerMode(AudioManager.RINGER_MODE_SILENT);
			    am.set(AlarmManager.RTC_WAKEUP, entry.eventStartTime, pi);
		    }
		    else {
			    am.set(AlarmManager.RTC_WAKEUP, entry.eventEndTime, pi);
		    }
	    }
    }
	
	public void cancelAlarm(Context context, boolean setNext) {
	    Intent intent = new Intent(context, Alarm.class);
	    PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
	    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	    alarmManager.cancel(sender);
	    
	    if (setNext) {
	    	setAlarm(context);
	    }
	} 
}