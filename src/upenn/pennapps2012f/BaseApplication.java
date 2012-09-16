package upenn.pennapps2012f;

import android.app.Application;

import com.facebook.android.Facebook;

public class BaseApplication extends Application {
	Facebook facebook = new Facebook("186880628113307");
	
	public Facebook getFacebook()
	{
		return facebook;
	}
}
