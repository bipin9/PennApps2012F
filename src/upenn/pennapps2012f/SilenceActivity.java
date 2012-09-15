package upenn.pennapps2012f;

import android.app.Activity;
import android.os.Bundle;

/**
 * Main activity that user interacts with.
 * User can turn on or off silence by flipping the
 * doorknob sign (tap or swipe) back and forth.
 * 
 * @author hoconnie, jinyan, ckong
 *
 */
public class SilenceActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.silence_activity);
	}

}
