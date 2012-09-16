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

        Bundle extras = intent.getExtras();
        String data = extras.getString("EventData");
        boolean occurred = extras.getBoolean("EventOccurred");
        System.out.println("ALARM HAPPENED with data " + data + " and occurred " + occurred);
        
        if (!occurred) {
        	// set timer for end time
        	((AudioManager)context.getSystemService(Context.AUDIO_SERVICE)).setRingerMode(AudioManager.RINGER_MODE_SILENT);
        	EventEntry current = new EventEntry(data);
        	
        	EventsDB db = new EventsDB(context);
        	db.open();
        	db.setCurrentEntry(current);
        	db.close();
        	
        	NotificationDB nDb = new NotificationDB(context);
        	nDb.open();
        	current.eventStartTime = System.currentTimeMillis();
        	nDb.addOccurredEvent(current);
        	nDb.close();
        	
        	setAlarmHelper(context, current, false);
        }
        else {
        	// reset for next event
        	((AudioManager)context.getSystemService(Context.AUDIO_SERVICE)).setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        	
        	EventsDB db = new EventsDB(context);
        	db.open();
        	db.setCurrentEntry(null);
        	db.close();
        	
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

	    setAlarmHelper(context, entry, true);
	}
    
    private void setAlarmHelper(Context context, EventEntry entry, boolean useStart) {
    	cancelAlarm(context);	// piece of shit code that made me loop for 4 hours, why wont alarm cancel itself -____- 
    	
    	if (entry != null) {
	    	AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	     
		    System.out.println("SETTING ALARM FOR " + entry.eventName + " with USESTART " + useStart);
		    
		    if (useStart) {
			    Intent i = new Intent(context, Alarm.class);
			    i.putExtra("EventData", entry.serialize());
			    
			    PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
			    am.set(AlarmManager.RTC_WAKEUP, entry.eventStartTime, pi);
		    }
		    else {
			    Intent i = new Intent(context, Alarm.class);
			    i.putExtra("EventOccurred", true);
			    
			    PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
			    am.set(AlarmManager.RTC_WAKEUP, entry.eventEndTime, pi);
		    }
	    }
    }
	
	public void cancelAlarm(Context context) {
	    Intent intent = new Intent(context, Alarm.class);
	    PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
	    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	    alarmManager.cancel(sender);
	    sender.cancel();
	}
}