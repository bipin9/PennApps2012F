package upenn.pennapps2012f;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * Feed that displays all notifications/events
 * that the user missed while silenced.
 * 
 * @author hoconnie, jinyan, ckong
 *
 */
public class NewsFeedActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.feed_activity);
	}
}
