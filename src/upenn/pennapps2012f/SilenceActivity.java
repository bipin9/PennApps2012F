package upenn.pennapps2012f;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.ImageView;

/**
 * Main activity that user interacts with.
 * User can turn on or off silence by flipping the
 * doorknob sign (tap or swipe) back and forth.
 * 
 * @author hoconnie, jinyan, ckong
 *
 */
public class SilenceActivity extends Activity implements OnClickListener {
	public boolean DO_NOT_DISTURB = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.silence_activity);

		// Set sign based on if on silence mode or not
		ImageView signView = (ImageView) findViewById(R.id.silenceSign);
		if (DO_NOT_DISTURB) {
			// Show do not disturb
			signView.setImageResource(R.drawable.silence_donotdisturb);
		} else {
			// Show available
			signView.setImageResource(R.drawable.silence_available);
		}

		// Gesture detection (swipe)
		final GestureDetector gestureDetector;
		View.OnTouchListener gestureListener;
		gestureDetector = new GestureDetector(new MyGestureDetector());
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};
		signView.setOnClickListener((android.view.View.OnClickListener) SilenceActivity.this); 
		signView.setOnTouchListener(gestureListener);
	}

	// On silence sign click: switch to off if on and vice versa
	public void onSilenceSignClick(View v) {
		ImageView signView = (ImageView) findViewById(R.id.silenceSign);
		if (DO_NOT_DISTURB) {
			// Set to available
			DO_NOT_DISTURB = false;
			signView.setImageResource(R.drawable.silence_available);
		} else {
			// Set to do not disturb
			DO_NOT_DISTURB = true;
			signView.setImageResource(R.drawable.silence_donotdisturb);
		}
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
				}  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				}
			} catch (Exception e) {
				// nothing
			}
			return false;
		}

	}

	public void onClick(View v) {
		onSilenceSignClick(v);
	}
}
