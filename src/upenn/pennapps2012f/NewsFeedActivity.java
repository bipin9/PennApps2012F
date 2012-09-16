package upenn.pennapps2012f;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Feed that displays all notifications/events
 * that the user missed while silenced.
 * 
 * @author hoconnie, jinyan, ckong
 *
 */
public class NewsFeedActivity extends Activity {
	private static final String logTag = "SwipeDetector";
	
	private ListView listView;
	private ArrayAdapter<Notification> adapter;

	public static enum Action {
		LR, // Left to Right
		RL, // Right to Left
		TB, // Top to bottom
		BT, // Bottom to Top
		None // when no action was detected
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.feed_activity);

		// Get all notifications
		DataRetrieval data = new DataRetrieval(this.getApplicationContext());
		data.getFacebookNotifications();
		data.getMailNotifications("connie.yw.ho@gmail.com", "hackathon");
		
		// Get listView resource
		listView = (ListView) findViewById(R.id.feed_list);

		NotificationDB db = new NotificationDB(this.getApplicationContext());
		db.open();
		Notification[] result = db.getAllNotifications(db.getFirstEventBeginningTime());
		db.resetOccurredEvents();
		db.close();
		
		if (result != null) {
			Notification[] n = result;

			Log.w("NOTIFICATION", "Number of notification loaded is " + result.length);
			
			List<Notification> nlist = new ArrayList<Notification>();
			for (int i = 0; i < result.length; i++) {
				nlist.add(n[i]);
			}
			
			adapter = new ContractAdapter(this, R.layout.feed_row, nlist);
			listView.setAdapter(adapter);
			
			// Set swipe detection for removing stories from feed
			final SwipeDetector swipeDetector = new SwipeDetector();
		    listView.setOnTouchListener(swipeDetector);
		    listView.setOnItemClickListener(new OnItemClickListener() {
		        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		                if (swipeDetector.swipeDetected()){
		                	Log.i(logTag, "swipe detected");

		                	Notification notif = adapter.getItem(position);
		                	NotificationDB db = new NotificationDB(NewsFeedActivity.this);
		                	db.open();
		                	db.deleteNotification(notif);
		                	db.close();
		                	
		                    // do the onSwipe action - remove the story from feed
		                	adapter.remove(notif);
		                	adapter.notifyDataSetChanged();
		                } else {
		                    // do the onItemClick action
		                	Log.i(logTag, "click detected");
		                	// Launch fb url if fb notification
		                	Notification notif = adapter.getItem(position);
		                	if (notif.type == Notification.FACEBOOK_TYPE && notif.link != null) {
		                		String url = notif.link;
		                		System.out.println("Launching url: " + url);
		                		Intent browserIntent = new Intent(Intent.ACTION_VIEW,
		                				Uri.parse(url));
		                		startActivity(browserIntent);
		                	}
		                }
		            }
		    });
		    listView.setOnItemLongClickListener(new OnItemLongClickListener() {
		        @Override
		        public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id) {
		            if (swipeDetector.swipeDetected()){
		            	Log.i(logTag, "swipe detected");
		            	Notification notif = adapter.getItem(position);
	                	NotificationDB db = new NotificationDB(NewsFeedActivity.this);
	                	db.open();
	                	db.deleteNotification(notif);
	                	db.close();
	                	
	                    // do the onSwipe action - remove the story from feed
	                	adapter.remove(notif);
	                	adapter.notifyDataSetChanged();
	                	return true;
		            } else {
		                // do the onItemLongClick action
		            	Log.i(logTag, "long press detected");
		            	return false;
		            }
		        }
		    });
		}
	}
	
	private class SwipeDetector implements View.OnTouchListener {

		private static final int MIN_DISTANCE = 100;
		private float downX, downY, upX, upY;
		private Action mSwipeDetected = Action.None;

		public boolean swipeDetected() {
			return mSwipeDetected != Action.None;
		}

		public Action getAction() {
			return mSwipeDetected;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				downX = event.getX();
				downY = event.getY();
				mSwipeDetected = Action.None;
				return false; // allow other events like Click to be processed
			case MotionEvent.ACTION_UP:
				upX = event.getX();
				upY = event.getY();

				float deltaX = downX - upX;
				float deltaY = downY - upY;

				// Horizontal swipe detection (left or right)
				if (Math.abs(deltaX) > MIN_DISTANCE) {
					if (deltaX < 0) {
						Log.i(logTag, "Swipe Left to Right");
						mSwipeDetected = Action.LR;
						return false;
					}
					if (deltaX > 0) {
						Log.i(logTag, "Swipe Right to Left");
						mSwipeDetected = Action.RL;
						return false;
					}
				} else if (Math.abs(deltaY) > MIN_DISTANCE) {
					//  Vertical swipe detection (up or down)
					if (deltaY < 0) {
						Log.i(logTag, "Swipe Top to Bottom");
						mSwipeDetected = Action.TB;
						return false;
					}
					if (deltaY > 0) {
						Log.i(logTag, "Swipe Bottom to Top");
						mSwipeDetected = Action.BT;
						return false;
					}
				}
				return false;
			}
			return false;
		}
	}
	
	class ContractAdapter extends ArrayAdapter<Notification> {

		private List<Notification> n;

		public ContractAdapter(Context context, int view, List<Notification> passedContracts) {
		        super(context, view, passedContracts);
		        n = passedContracts;
		}

		@Override
		public int getCount() {
		    return n.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
		        View currentView = convertView;
		        LayoutInflater currentViewInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		        currentView = currentViewInflater.inflate(R.layout.feed_row, null);
		        Notification currentNotification = n.get(position);

		        if (currentNotification != null) {
			        // Add info
			        TextView title = (TextView)currentView.findViewById(R.id.row_title);
			        title.setText((currentNotification.subject == null) ? "" : currentNotification.subject);
			        
			        TextView body = (TextView)currentView.findViewById(R.id.row_text);
			        body.setText((currentNotification.message == null) ? "" : currentNotification.message);
			        if (currentNotification.message == null || currentNotification.message.equals("")) {
			        	body.setVisibility(View.GONE);
			        }
			        
			        ImageView image = (ImageView)currentView.findViewById(R.id.row_footer_icon);
			        TextView from = (TextView)currentView.findViewById(R.id.row_footer_from_field);
			        switch (currentNotification.type) {
			        case Notification.EMAIL_TYPE:
			        	image.setBackgroundResource(R.drawable.feed_email);
			        	from.setText("EMAIL FROM " + currentNotification.sender.toUpperCase());
			        	break;
			        case Notification.FACEBOOK_TYPE:
			        	image.setBackgroundResource(R.drawable.feed_facebook);
			        	from.setText("FACEBOOK NOTIFICATION FROM " + currentNotification.sender.toUpperCase());
			        	break;
			        case Notification.SMS_TYPE:
			        	image.setBackgroundResource(R.drawable.feed_msg);
			        	from.setText("SMS FROM " + currentNotification.sender.toUpperCase());
			        	break;
			        case Notification.TWITTER_TYPE:
			        	image.setBackgroundResource(R.drawable.feed_msg);
			        	from.setText("TWEET FROM " + currentNotification.sender.toUpperCase());
			        	break;
			        default:
			        	break;
			        }
			        
			        TextView time = (TextView)currentView.findViewById(R.id.row_footer_time);
			        Date t = new Date(currentNotification.time); 
			        if (t.getHours() > 12) 
			        	time.setText(" @ " + t.getMonth() + "/" + t.getDate() + " " + (t.getHours() - 12) + ":" + ((t.getMinutes() < 10) ? ("0" + t.getMinutes()) : t.getMinutes()) + "PM");
			        else
			        	time.setText(" @ " + t.getMonth() + "/" + t.getDate() + " " + t.getHours() + ":" + ((t.getMinutes() < 10) ? ("0" + t.getMinutes()) : t.getMinutes()) + "AM");
			        
			        System.out.println("Logging: source - " + currentNotification.sender + ", title - " + currentNotification.subject + 
			        		", message - " + currentNotification.message + ", time - " + currentNotification.time);
		        }
		        return currentView;
		}
	}
}

