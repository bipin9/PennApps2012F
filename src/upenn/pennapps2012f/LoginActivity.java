package upenn.pennapps2012f;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

/**
 * Prompts user to log into their Google account
 * to sync with GCal and GMail.
 * User only sees this the first time the application is launched.
 * 
 * @author hoconnie, jinyan, ckong
 *
 */
public class LoginActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_login, menu);
        return true;
    }
}
