package upenn.pennapps2012f;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
		
		EventEntry entry = ((MyApplication)context.getApplicationContext()).getCurrent();
		String number = smsMessage[0].getOriginatingAddress();
		System.out.println("Received message: " + smsMessage[0].getMessageBody() + " - SILENCE IS " + ((entry != null) ? "ON" : "OFF"));
		if (entry != null && number != null) {
			SMSHelper help = new SMSHelper(context);
			help.SendSMS(number, entry);
		}
	}

}
