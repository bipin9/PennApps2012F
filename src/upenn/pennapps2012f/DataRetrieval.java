package upenn.pennapps2012f;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;

public class DataRetrieval {

	Facebook facebook;
	Context mCtx;
	
	public DataRetrieval(Context context) {
		System.out.println("Initiating DataRetrieval object");
		facebook = ((BaseApplication)context).getFacebook();
		System.out.println("Got facebook object");
		mCtx = context;
	}
	
	public long parseFBtime(String time)
	{
		time = time.substring(0, 19);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
      		   Log.v("notifications",response);
      		   try {
	      		   JSONObject jsonObjectResults = new JSONObject(response);
	      		   JSONArray jsonNotificationDataArray = jsonObjectResults.getJSONArray("data");
	      		   	      		   
	      		   Notification[] result = new Notification[jsonNotificationDataArray.length()];
		      	   for (int i=0;i<jsonNotificationDataArray.length();i++)
		      	   {
		      		    JSONObject notification = jsonNotificationDataArray.getJSONObject(i);
		      		    String from = notification.getJSONObject("from").getString("name");
		      		    String date = notification.getString("created_time");
		      		    String message = notification.getString("title");
		      		    String link = notification.getString("link");
		      		    long time = parseFBtime(date);
		      		    Notification entry = new Notification(Notification.FACEBOOK_TYPE, (message == null) ? "" : message, "", link, time, from);
		      		    result[i] = entry;
		      	   }
		      	   
		      	   System.out.println("FACEBOOK ADDED " + result.length + " ENTRIES");

	      		   NotificationDB db = new NotificationDB(mCtx);
	      		   db.open();
	      		   db.addNotification(result);
		      	   db.close();
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
		new GetMailAsync().execute(username, password);
	}
	
	class GetMailAsync extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... arg0) {
			System.out.println("USERNAME " + arg0[0] + " password " + arg0[1]);
			Notification[] list;
			Properties props = System.getProperties();
			props.setProperty("mail.store.protocol", "imaps");
				try {
					Session session = Session.getDefaultInstance(props, null);
					Store store = session.getStore("imaps");
					store.connect("imap.gmail.com", arg0[0], arg0[1]);
					
					Folder inbox = store.getFolder("Inbox");
					inbox.open(Folder.READ_ONLY);
					FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);

					Message messages[] = inbox.search(ft);
					list = new Notification[messages.length];
					int index = 0;
					for(Message message:messages) {
						String from = "";
						for(Address a: message.getFrom())
						{
							from = a.toString();
						}

//						Object content = message.getContent();
//						String body = null;
//						// Grab the body content text
//						if ( content instanceof String ) 
//						    body = (String) content;
						Notification n = new Notification(Notification.EMAIL_TYPE, message.getSubject(), "", "", message.getReceivedDate().getTime(), from);
						list[index++] = n;
					}
					
					// Add to DB
					NotificationDB db = new NotificationDB(mCtx);
					db.open();
					db.addNotification(list);
					db.close();
					
			} catch (NoSuchProviderException e) {
				e.printStackTrace();
				
			} catch (MessagingException e) {
				e.printStackTrace();
			
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
				
				return null;
		}
		
	}
	
}
