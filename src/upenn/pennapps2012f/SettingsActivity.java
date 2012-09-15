package upenn.pennapps2012f;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * Settings activity that can be reached from icon
 * on SilenceActivity. Allows user to set favorites
 * (exceptions to the silence rule) and to customize
 * additional features (exception rules, custom
 * auto-reply message, etc.)
 * 
 * @author hoconnie, jinyan, ckong
 *
 */

public class SettingsActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settings_activity);
	}

}
