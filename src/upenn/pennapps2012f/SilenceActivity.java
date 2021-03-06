package upenn.pennapps2012f;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.ImageView;

import com.facebook.android.Facebook;

/**
 * Main activity that user interacts with.
 * User can turn on or off silence by flipping the
 * doorknob sign (tap or swipe) back and forth.
 * 
 * @author hoconnie, jinyan, ckong
 *
 */
public class SilenceActivity extends Activity {
	public static final int SILENCE_ID = 100;

	private AnimationDrawable signAnimation;
	private ImageView signView;
	
	private SharedPreferences mPrefs;
	private Facebook facebook;
	
	private boolean DO_NOT_DISTURB;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.silence_activity);

		new AsyncLoadCalendars(this.getApplicationContext()).execute(this.getContentResolver());
		new LoadCalendarAlarm().setAlarm(this.getApplicationContext());

		// Set sign based on if on silence mode or not
		signView = (ImageView) findViewById(R.id.silenceSign);
		
		signAnimation = (AnimationDrawable) signView.getBackground();

		// On click listener for tapping on sign
		signView.setOnClickListener(new OnClickListener() {
			// On silence sign click: switch to off if on and vice versa
			public void onClick(View v) {
				ImageView signView = (ImageView) findViewById(R.id.silenceSign);
				
				if (DO_NOT_DISTURB) {
					// Set to available
					DO_NOT_DISTURB = false;
					
					// Reset alarm
					Context context = SilenceActivity.this.getApplicationContext();
					new Alarm().cancelAlarm(context);

		        	// reset for next event
		        	((AudioManager)context.getSystemService(Context.AUDIO_SERVICE)).setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		        	
		        	EventsDB db = new EventsDB(context);
		        	db.open();
		        	db.setCurrentEntry(null);
		        	db.close();

					signView.setBackgroundResource(R.drawable.away_to_available_animation);
					signAnimation = (AnimationDrawable) signView.getBackground();
					signAnimation.start();
				} else {
					// Set to do not disturb
					DO_NOT_DISTURB = true;
					
					new Alarm().setAlarm(SilenceActivity.this.getApplicationContext());

					signView.setBackgroundResource(R.drawable.available_to_away_animation);
					signAnimation = (AnimationDrawable) signView.getBackground();
					signAnimation.start();
				}
			}
		});

		// On click listener for tapping on heart icon to go to settings
		ImageView favIcon = (ImageView) findViewById(R.id.favorites_icon);
		final Intent i = new Intent(this, SettingsActivity.class);
		favIcon.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				startActivity(i);
			}
		});

		// On click listener for tapping on fb icon to login with fb
		ImageView fbIcon = (ImageView) findViewById(R.id.facebook_icon);
		fbIcon.setOnClickListener(new OnClickListener() {
			// TODO Charles
			@Override
			public void onClick(View v) {
//				System.out.println("Starting facebook auth");
//				facebook = ((BaseApplication)getApplicationContext()).getFacebook();
//				System.out.println("Acquired facebook");
//				mPrefs = getPreferences(MODE_PRIVATE);
//				String access_token = mPrefs.getString("access_token", null);
//				long expires = mPrefs.getLong("access_expires", 0);
//				if(access_token != null) {
//					System.out.println("Setting access token");
//					facebook.setAccessToken(access_token);
//				}
//				if(expires != 0) {
//					System.out.println("Setting access expires");
//					facebook.setAccessExpires(expires);
//				}
//
//				System.out.println("STEP");
//				/*
//				 * Only call authorize if the access_token has expired.
//				 */
//				if(!facebook.isSessionValid()) {
//
//					System.out.println("STEP1");
//					
//					facebook.authorize(SilenceActivity.this, new String[] {"manage_notifications"}, new DialogListener() {
//						public void onComplete(Bundle values) {
//							System.out.println("STEP2");
//							
//							SharedPreferences.Editor editor = mPrefs.edit();
//							editor.putString("access_token", facebook.getAccessToken());
//							editor.putLong("access_expires", facebook.getAccessExpires());
//							editor.commit();
//
//						}
//
//						public void onFacebookError(FacebookError error) {
//
//							System.out.println("STEP FBERROR");
//						}
//
//						public void onError(DialogError e) {
//							System.out.println("STEP ERROR");}
//
//						public void onCancel() {
//							System.out.println("STEP CANCEL");}
//					});
//				}

				startActivity(new Intent(SilenceActivity.this, FacebookAuthActivity.class));
			}
		});

		// Gesture detection for swiping sign
		final GestureDetector gestureDetector;
		View.OnTouchListener gestureListener;
		gestureDetector = new GestureDetector(new MyGestureDetector());
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};
		signView.setOnTouchListener(gestureListener);

		new Runnable() {
			public void run() 
			{
				showNotification();
			}
		}.run();

//		// TESTING
//		EventsDB db = new EventsDB(this.getApplicationContext());
//		db.open();
//		db.initializeTestData();
//		db.close();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		// Set sign based on if on silence mode or not
		EventsDB db = new EventsDB(SilenceActivity.this.getApplicationContext());
    	db.open();
    	EventEntry entry = db.getCurrentEntry();
    	db.close();
    	
		if (entry != null) {
			// Show do not disturb
			System.out.println("Setting sign to DO NOT DISTURB");
			DO_NOT_DISTURB = true;
			signView.setBackgroundResource(R.drawable.away_to_available_animation);
		} else {
			// Show available
			System.out.println("Setting sign to AVAILABLE");
			DO_NOT_DISTURB = false;
			signView.setBackgroundResource(R.drawable.available_to_away_animation);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		((BaseApplication)this.getApplicationContext()).getFacebook().authorizeCallback(requestCode, resultCode, data);
	}

	private void showNotification() {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

		int icon = R.drawable.feed_email;	// TODO CHANGE
		CharSequence tickerText = "Silencr";
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);

		notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR; 

		
		CharSequence contentTitle = "Silencr";
		CharSequence contentText = "See what you missed";
		Intent notificationIntent = new Intent(this, NewsFeedActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		notification.setLatestEventInfo(this, contentTitle, contentText, contentIntent);

		System.out.println("Showing the notification");

		mNotificationManager.notify(SILENCE_ID, notification);
	}

	// Gesture detection for swipes
	private class MyGestureDetector extends SimpleOnGestureListener {
		final ViewConfiguration vc = ViewConfiguration.get(getApplicationContext());
		final int SWIPE_MIN_DISTANCE = vc.getScaledTouchSlop();
		final int SWIPE_THRESHOLD_VELOCITY = vc.getScaledMinimumFlingVelocity();
		final int SWIPE_MAX_OFF_PATH = 250;

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe
				if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					// right swipe
				}  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					// left swipe
				}
			} catch (Exception e) {
				// nothing
			}
			return false;
		}
	}
}
