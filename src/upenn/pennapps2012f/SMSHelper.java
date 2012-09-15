package upenn.pennapps2012f;

import java.util.Date;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.telephony.SmsManager;

public class SMSHelper {

	String accountName;
	
	public SMSHelper(Context context) {
		Account[] accounts = AccountManager.get(context).getAccounts();
		for (Account account : accounts) {
			if(account.type.equalsIgnoreCase("com.google")) {
				accountName = account.name;
				break;
			}
		}
	}
	
	public void SendSMS(String smsNumber, EventEntry currentEvent) {
		SmsManager smsManager = SmsManager.getDefault();

		String smsText = accountName + ": cannot reply to this text right now because I am at: " +
				currentEvent.eventName + " until " + new Date(currentEvent.eventEndTime).toString();
	    smsManager.sendTextMessage(smsNumber, null, smsText, null, null);
	}
	
}
