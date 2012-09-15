package upenn.pennapps2012f;

public class Notification {
	
	public final static String EMAIL_ID = "email";
	public final static String SMS_ID = "sms";
	public final static String FACEBOOK_ID = "facebook";
	public final static String TWITTER_ID = "twitter";
	
	public final static int EMAIL_TYPE = 0;
	public final static int SMS_TYPE = 1;
	public final static int FACEBOOK_TYPE = 2;
	public final static int TWITTER_TYPE = 3;
	
	public int id;
	public int type;	// refer above
	public String message;
	public long time;
	public String sender;
	
	public Notification() {
	}
	
	public Notification(int _id, int _type, String _message, long _time, String _sender) {
		id = _id;
		type = _type;
		message = _message;
		time = _time;
		sender = _sender;
	}
}
