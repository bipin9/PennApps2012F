package upenn.pennapps2012f;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public class Alarm extends BroadcastReceiver {    
	
	@Override
     public void onReceive(Context context, Intent intent) {   
         PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
         PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
         wl.acquire();

         // Put here YOUR code.
         System.out.println("ALARM HAPPENED");
         SetAlarm(context);
         
         wl.release();
     }

     public void SetAlarm(Context context) {    	 
    	 AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	     Intent i = new Intent(context, Alarm.class);
	     PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
	     
	     // Get the next time
	     EventsDB db = new EventsDB(context);
	     db.open();
	     EventEntry entry = db.getNextEntry();
	     db.close();
	     
	     if (entry != null) {
		     System.out.println("SETTING ALARM FOR " + entry.eventName);
		     am.set(AlarmManager.RTC_WAKEUP, entry.eventStartTime, pi);
	     }
	 }
	
	 public void CancelAlarm(Context context, boolean setNext) {
	     Intent intent = new Intent(context, Alarm.class);
	     PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
	     AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	     alarmManager.cancel(sender);
	     
	     if (setNext) {
	    	 SetAlarm(context);
	     }
	 }
	 
}