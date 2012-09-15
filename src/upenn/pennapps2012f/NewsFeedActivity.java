package upenn.pennapps2012f;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
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
	private ListView listView;
	private ArrayAdapter<String> listAdapter;

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
	}
}
