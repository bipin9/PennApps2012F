package upenn.pennapps2012f;

import android.app.Application;

import com.facebook.android.Facebook;

public class BaseApplication extends Application {
	Facebook facebook = new Facebook("142646962492486");
	
	public Facebook getFacebook()
	{
		return facebook;
	}
}
