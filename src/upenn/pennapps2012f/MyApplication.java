package upenn.pennapps2012f;

import android.app.Application;

public class MyApplication extends Application {
	private EventEntry currentEvent;
	
	public EventEntry getCurrent() {
		System.out.println("DEBUG!!!!!! Getting current event: " + ((currentEvent == null) ? "null" : currentEvent.eventName));
		return currentEvent;
	}
	
	public void setCurrent(EventEntry current) {
		currentEvent = current;
		System.out.println("DEBUG!!!!!! Setting current event to " + ((currentEvent == null) ? "null" : currentEvent.eventName));
	}
}
