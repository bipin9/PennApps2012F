package upenn.pennapps2012f;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

/**
 * Prompts user to log into their Google account
 * to sync with GCal and GMail.
 * User only sees this the first time the application is launched.
 * 
 * @author hoconnie, jinyan, ckong
 *
 */
public class LoginActivity extends Activity {
	private AccountManager manager;
	private DefaultHttpClient http_client = new DefaultHttpClient();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		// Get users Google account
//		Account account = getGoogleAccount();
//		if (account == null) {
//			// TODO Do something if the account retrieval failed
//			Log.wtf("gmail", "NO ACCOUNT");
//		}
//		else {
//			Log.wtf("gmail", account.toString());
//			// Get auth token
//			manager.getAuthToken(account, "ah", false, new GetAuthTokenCallback(), null);
//		}
		
		// TODO: move this to sync with gcal
//		EventsDB db = new EventsDB(this.getApplicationContext());
//		db.open();
//		db.initializeTestData();
//		db.close();
		
		new AsyncLoadCalendars(this.getApplicationContext()).execute(this.getContentResolver());
		new LoadCalendarAlarm().setAlarm(this.getApplicationContext());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}

	// Get the Google account that the user is logged into the phone with
	private Account getGoogleAccount() {
		manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
		Account[] list = manager.getAccounts();
		for (Account account : list) {
			if(account.type.equalsIgnoreCase("com.google")) {
				return account;
			}
		}
		return null;
	}

	// Callback after auth token is retrieved.
	private class GetAuthTokenCallback implements AccountManagerCallback {
		public void run(AccountManagerFuture result) {
			Bundle bundle;
			try {
				bundle = (Bundle) result.getResult();
				Intent intent = (Intent)bundle.get(AccountManager.KEY_INTENT);
				if (intent != null) {
					// User input required
					startActivity(intent);
				} else {
					// User input not required
					onGetAuthToken(bundle);
				}
			} catch (OperationCanceledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AuthenticatorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private void onGetAuthToken(Bundle bundle) {
			String auth_token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
			// Get auth cookie which is used in all future requests
			new GetCookieTask().execute(auth_token);
		}
	}

	// Used to trade in authentication token for cookie
	private class GetCookieTask extends AsyncTask {
		protected Boolean doInBackground(String... tokens) {
			try {
				// Don't follow redirects
				http_client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);

				HttpGet http_get = new HttpGet("https://yourapp.appspot.com/_ah/login?continue=http://localhost/&auth=" + tokens[0]);
				HttpResponse response;
				response = http_client.execute(http_get);
				if(response.getStatusLine().getStatusCode() != 302)
					// Response should be a redirect
					return false;

				for(Cookie cookie : http_client.getCookieStore().getCookies()) {
					if(cookie.getName().equals("ACSID"))
						return true;
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				http_client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
			}
			return false;
		}

		// Fetch URL from site using new authentication cookie
		protected void onPostExecute(Boolean result) {
			new AuthenticatedRequestTask().execute("http://yourapp.appspot.com/admin/");
		}

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	// Auth cookie now stored in HttpClient
	private class AuthenticatedRequestTask extends AsyncTask {
		@Override
		protected Object doInBackground(Object... urls) {
			try {
				HttpGet http_get = new HttpGet((URI) urls[0]);
				return http_client.execute(http_get);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPostExecute(HttpResponse result) {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(result.getEntity().getContent()));
				String first_line = reader.readLine();
				Toast.makeText(getApplicationContext(), first_line, Toast.LENGTH_LONG).show();                          
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
