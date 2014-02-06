package fr.emn.android.chat.activity;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import fr.emn.android.chat.R;
import fr.emn.chat.android.utilitaires.InputStreamToBoolean;

public class MainActivity extends Activity {

	private static String TAG = "MainActivity";
	private String username;
	private String password;
	private LoginTask loginTask;
	private SharedPreferences.Editor editor;

	private EditText etUsername;
	private EditText etPassword;
	private Button btnReset;
	private Button btnSubmit;
	private TextView tvErrorMessage;
	private ProgressBar pbWheel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		etUsername = (EditText) findViewById(R.id.etuser);
		etPassword = (EditText) findViewById(R.id.etpassword);
		btnReset = (Button) findViewById(R.id.btnreset);
		btnSubmit = (Button) findViewById(R.id.btnsubmit);
		tvErrorMessage = (TextView) findViewById(R.id.tverrormessage);
		pbWheel = (ProgressBar) findViewById(R.id.pdWheel);
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		editor = preferences.edit();
		username = preferences.getString(getString(R.string.preferenceUsername), null);
		password = preferences.getString(getString(R.string.preferencePassword), null);

		Log.i(TAG, username + "/" + password);

		if(username == null || password == null) {
			vider();
			envoyer();
		}
		else{
			etUsername.setText(username);
			etPassword.setText(password);
			
			vider();
			envoyer();
		}


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/***********************************************************************************************/
	/** MY METHOD                                                                                 **/
	/***********************************************************************************************/
	private void vider() {
		btnReset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				etUsername.setText("");
				etPassword.setText("");
			}
		});
	}

	private void envoyer() {
		btnSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v){ 
				
				Log.i(TAG, username + "/" + password);
				if(username == null || password == null) {
					username = etUsername.getText().toString();
					password = etPassword.getText().toString();

					Log.i(TAG, "2" + username + "/" + password);

					editor.putString(getString(R.string.preferenceUsername), username);
					editor.putString(getString(R.string.preferencePassword), password);
					editor.commit();
				}

				//Log.i("TEST", loginTask.getStatus().toString());
				if(loginTask == null || loginTask.getStatus() == Status.FINISHED) {
					/*String username = eUsername.getText().toString();
					String password = ePassword.getText().toString();


					if (username.length() > 0 && password.length() > 0)
					{*/
					loginTask = new LoginTask();
					/*}
					else
					{
						Toast.makeText(getApplicationContext(), getText(R.string.error), Toast.LENGTH_SHORT).show();
					}*/
				}

				if(loginTask.getStatus() == Status.PENDING) {
					loginTask.execute();
				}
			}
		});
	}






	private class LoginTask extends AsyncTask {

		@Override
		protected void onPreExecute() {
			pbWheel.setVisibility(View.VISIBLE);
		}

		@Override
		protected Boolean doInBackground(Object... params) {
			// ex??cut?? sur un autre thread
			Boolean bContent = false;

			try {
				DefaultHttpClient client = new DefaultHttpClient();
				String url = "http://parlezvous.herokuapp.com/connect/" + username +"/" + password;
				HttpGet request = new HttpGet(url);
				Log.i("TEST", url);
				HttpResponse response = client.execute(request);

				InputStream content = response.getEntity().getContent();
				bContent = InputStreamToBoolean.convert(content);

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return bContent;
		}

		@Override
		protected void onPostExecute(Object resuloginTask) {
			// ex??cut?? dans l'UIThread

			pbWheel.setVisibility(View.INVISIBLE);
			btnSubmit.setEnabled(true);

			if((Boolean) resuloginTask){
				Toast.makeText(getApplicationContext(), getString(R.string.log_confirm), 
						Toast.LENGTH_SHORT).show();

				Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
				intent.putExtra(getString(R.string.preferenceUsername), username);
				intent.putExtra(getString(R.string.preferencePassword), password);
				startActivity(intent);
				//finish();
			}
			else{
				Toast.makeText(getApplicationContext(), getString(R.string.log_not_confirm), 
						Toast.LENGTH_SHORT).show();
			}


		}
	}
}
