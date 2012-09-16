package upenn.pennapps2012f;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.*;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.AsyncFacebookRunner.RequestListener;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;

import android.content.Context;
import android.util.Log;

public class DataRetrieval {

	Facebook facebook;
	public DataRetrieval(Context context) {
		facebook = ((BaseApplication)context.getApplicationContext()).getFacebook();
		
	}
	
	public long parseFBtime(String time)
	{
		time = time.substring(0, 19);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss");
		try {
			Date date = df.parse(time);
		    long epoch = date.getTime();
		    System.out.println(epoch); // 
		    return epoch;
		}
		catch (ParseException e) { return -1;}
		
	}
	public void getFacebookNotifications()
	{
		 AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);
		 mAsyncRunner.request("me/notifications", new RequestListener() {
      	   //called on successful completion of the Request
      	   public void onComplete(final String response, final Object state){
      		   Notification[] notifications;
      		   Log.v("notifications",response);
      		   try {
	      		   JSONObject jsonObjectResults = new JSONObject(response);
	      		   JSONArray jsonNotificationDataArray = jsonObjectResults.getJSONArray("data");
	      		   notifications = new Notification[jsonNotificationDataArray.length()];
		      	   for (int i=0;i<jsonNotificationDataArray.length();i++)
		      	   {
		      		    JSONObject notification = jsonNotificationDataArray.getJSONObject(i);
		      		    String from = notification.getJSONObject("from").getString("name");
		      		    String date = notification.getString("created_time");
		      		    String message = notification.getString("title");
		      		    String link = notification.getString("link");
		      		    long time = parseFBtime(date);
		      		    Notification entry = new Notification(Notification.FACEBOOK_TYPE, message, null, link, time, from);
		      		    notifications[i] = entry;
		      	   }
      		   }
      		   catch (JSONException e) {}
      		   
      	   }

      	   public void onIOException(IOException e,
					Object state) {
				// TODO Auto-generated method stub
				
      	   }

      	   public void onFileNotFoundException(
					FileNotFoundException e, Object state) {
				// TODO Auto-generated method stub
				
      	   }

			public void onMalformedURLException(
					MalformedURLException e, Object state) {
				// TODO Auto-generated method stub
				
			}

			public void onFacebookError(FacebookError e,
					Object state) {
				// TODO Auto-generated method stub
				
			}
      }
      
      );
	}
	
	public void getMailNotifications(String username, String password)
	{
		ArrayList<Notification> list = new ArrayList<Notification>();
		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");
			try {
				Session session = Session.getDefaultInstance(props, null);
				Store store = session.getStore("imaps");
				store.connect("imap.gmail.com", username, password);
				
				Folder inbox = store.getFolder("Inbox");
				inbox.open(Folder.READ_ONLY);
				FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);

				Message messages[] = inbox.search(ft);
				for(Message message:messages) {
					String from = "";
					for(Address a: message.getFrom())
					{
						from = a.toString();
					}
					Object content = message.getContent();
					String body = null;
					// Grab the body content text
					if ( content instanceof String ) 
					    body = (String) content;
					Notification n = new Notification(Notification.EMAIL_TYPE, message.getSubject(), body, null, message.getReceivedDate().getTime(), from);
					list.add(n);
					
				}
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			
		} catch (MessagingException e) {
			e.printStackTrace();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
