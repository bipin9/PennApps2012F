package upenn.pennapps2012f;

public class EventEntry {

	public int id;
	public String eventName;
	public long eventStartTime;
	public long eventEndTime;
	
	private static final String TOKEN_SEPARATOR = "dhjsaladkjalsdlswehoqe";
	
	public EventEntry() {
	}
	
	public EventEntry(String toDeserialize) {
		deserialize(toDeserialize);
	}
	
	public String serialize() {
		StringBuffer buf = new StringBuffer();
		buf.append(id);
		buf.append(TOKEN_SEPARATOR);
		buf.append(eventName);
		buf.append(TOKEN_SEPARATOR);
		buf.append(eventStartTime);
		buf.append(TOKEN_SEPARATOR);
		buf.append(eventEndTime);
		
		return buf.toString();
	}
	
	private void deserialize(String input) {
		String[] tokens = input.split(TOKEN_SEPARATOR);
		id = Integer.parseInt(tokens[0]);
		eventName = tokens[1];
		eventStartTime = Long.parseLong(tokens[2]);
		eventEndTime = Long.parseLong(tokens[3]);
	}
}
