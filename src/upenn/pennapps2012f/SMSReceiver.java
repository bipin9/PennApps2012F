package upenn.pennapps2012f;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		
		Object messages[] = (Object[]) bundle.get("pdus");
		SmsMessage smsMessage[] = new SmsMessage[messages.length];
		for (int n = 0; n < messages.length; n++) {
			smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
		}

    	EventsDB db = new EventsDB(context);
    	db.open();
    	EventEntry entry = db.getCurrentEntry();
    	db.close();

		String number = smsMessage[0].getOriginatingAddress();
		
		System.out.println("Received message: " + smsMessage[0].getMessageBody() + " - SILENCE IS " + ((entry != null) ? "ON" : "OFF"));
		if (entry != null && number != null) {
	    	Notification n = new Notification();
	    	n.subject = smsMessage[0].getMessageBody();
	    	n.sender = getContactDisplayNameByNumber(context, number);
	    	n.message = "";
	    	n.time = smsMessage[0].getTimestampMillis();
	    	n.type = Notification.SMS_TYPE;
	    	
	    	NotificationDB nDb = new NotificationDB(context);
	    	nDb.open();
	    	nDb.addNotification(n);
	    	nDb.close();
	    	
			SMSHelper help = new SMSHelper(context);
			help.SendSMS(number, entry);
		}
	}

	public String getContactDisplayNameByNumber(Context context, String number) {
	    Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
	    String name = "?";

	    ContentResolver contentResolver = context.getContentResolver();
	    Cursor contactLookup = contentResolver.query(uri, new String[] {BaseColumns._ID,
	            ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

	    try {
	        if (contactLookup != null && contactLookup.getCount() > 0) {
	            contactLookup.moveToNext();
	            name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
	            //String contactId = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
	        }
	    } finally {
	        if (contactLookup != null) {
	            contactLookup.close();
	        }
	    }

	    return name;
	}
}
