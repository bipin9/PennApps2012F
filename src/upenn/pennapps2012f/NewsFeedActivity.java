package upenn.pennapps2012f;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
	private ArrayAdapter<String> listAdapter;

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

		// Get listView resource
		listView = (ListView) findViewById(R.id.feed_list);

		// Create and populate list of story titles
		String[] titles = new String[] {
				"Re: PennApps logistics",
				"Want to get lunch later today?",
				"Charles Kong wrote on your timeline",
				"What's our 552 homework?",
				"Jinyan Cao liked your status",
				"Re: 501 meeting"
		};
		ArrayList<String> titleList = new ArrayList<String>();
		titleList.addAll(Arrays.asList(titles));

		// Create ArrayAdapter using the title list
		listAdapter = new ArrayAdapter<String>(this, R.layout.feed_row, R.id.row_title, titleList);

		// Set ArrayAdapter as ListView's adapter
		listView.setAdapter(listAdapter);
		
		// Set swipe detection for removing stories from feed
		final SwipeDetector swipeDetector = new SwipeDetector();
	    listView.setOnTouchListener(swipeDetector);
	    listView.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	                if (swipeDetector.swipeDetected()){
	                	Log.i(logTag, "swipe detected");
	                	// TODO Jin remove from db
	                    // do the onSwipe action - remove the story from feed
	                	listAdapter.remove(listAdapter.getItem(position));
	                	listAdapter.notifyDataSetChanged();
	                } else {
	                    // do the onItemClick action
	                	Log.i(logTag, "click detected");
	                }
	            }
	    });
	    listView.setOnItemLongClickListener(new OnItemLongClickListener() {
	        @Override
	        public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id) {
	            if (swipeDetector.swipeDetected()){
	            	Log.i(logTag, "swipe detected");
                	// TODO Jin remove from db
	                // do the onSwipe action - remove the story from feed
	            	listAdapter.remove(listAdapter.getItem(position));
                	listAdapter.notifyDataSetChanged();
	            	return true;
	            } else {
	                // do the onItemLongClick action
	            	Log.i(logTag, "long press detected");
	            	return true;
	            }
	        }
	    });
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
}

