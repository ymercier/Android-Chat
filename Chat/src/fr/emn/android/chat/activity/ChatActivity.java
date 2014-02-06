package fr.emn.android.chat.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import fr.emn.android.chat.R;
import fr.emn.android.chat.adapter.ChatAdapter;
import fr.emn.android.chat.resource.Message;
import fr.emn.chat.android.utilitaires.InputStreamToString;
import fr.emn.chat.android.utilitaires.MessageParser;

public class ChatActivity extends Activity {

	private static String TAG = "ChatActivity";
	private String username;
	private String password;
	private String addMessage;
	private UpdateMessages update;
	private SendMessage send;
	private List<Message> messages;


	//private ListView lvchat;
	private PullToRefreshListView plvChat;
	private ProgressBar pbMessages;
	private Button btnSend;
	private EditText etAddMessage;
	private Button btnHistorique;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		Intent intent = getIntent();
		username = intent.getStringExtra(getString(R.string.preferenceUsername));
		password = intent.getStringExtra(getString(R.string.preferencePassword));

		Log.i(TAG, "in onCreate");
		//lvchat = (ListView) findViewById(R.id.lvchat);
		plvChat = (PullToRefreshListView) findViewById(R.id.lvchat);
		pbMessages = (ProgressBar) findViewById(R.id.pbmessage);
		btnSend = (Button) findViewById(R.id.btnsend);
		etAddMessage = (EditText) findViewById(R.id.etaddmessage);
		btnHistorique = (Button) findViewById(R.id.btnhistorique);

		if (update == null || update.getStatus() == Status.FINISHED) {
			update = new UpdateMessages();
		}
		if (update.getStatus() == Status.PENDING)
			update.execute();
		
		
		refresh();
		send();
		historique();
	}

	private void send() {
		btnSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				addMessage = etAddMessage.getText().toString();

				if(addMessage != "" && addMessage != "") {
					if (send == null || send.getStatus() == Status.FINISHED) {
						send = new SendMessage();
					}
					if (send.getStatus() == Status.PENDING)
						send.execute();
				}
				else
				{
					Toast.makeText(getApplicationContext(), "@string/erroraddmessage", 50);
				}
			}
		});

	}
	
	private void historique(){
		btnHistorique.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
	}
	
	private void refresh() {
		plvChat.setOnRefreshListener(new OnRefreshListener<ListView>() {
		    @Override
		    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		        // Do work to refresh the list here.
		    	if (update == null || update.getStatus() == Status.FINISHED) {
					update = new UpdateMessages();
				}
				if (update.getStatus() == Status.PENDING)
					update.execute();
		    }
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}


	/******************************************************************************************************/
	/** AsynTask pour faire un update des messages dans le listview                                      **/
	/******************************************************************************************************/
	private class UpdateMessages extends AsyncTask {

		@Override
		protected void onPreExecute() {
			pbMessages.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(Object... params) {
			String sContent = "";

			try {
				DefaultHttpClient client = new DefaultHttpClient();
				String url = "http://parlezvous.herokuapp.com/messages/" + username +"/" + password;
				HttpGet request = new HttpGet(url);
				HttpResponse response = client.execute(request);

				InputStream content = response.getEntity().getContent();
				sContent = InputStreamToString.convert(content);


			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return sContent;
		}

		@Override
		protected void onPostExecute(Object result) {

			pbMessages.setVisibility(View.INVISIBLE);

			if(!((String) result).trim().isEmpty()){
				messages = MessageParser.parser((String) result);

				Log.i(TAG, (String) result);

				if(messages != null) {
					ChatAdapter adapter = new ChatAdapter(getApplicationContext(), messages);
					//lvCcat.setAdapter(adapter);
					plvChat.setAdapter(adapter);
				}
			}
		}
	}


	/******************************************************************************************************/
	/** AsynTask pour envoyer un message                                                                 **/
	/******************************************************************************************************/
	private class SendMessage extends AsyncTask {

		@Override
		protected void onPreExecute() {
			pbMessages.setVisibility(View.VISIBLE);
		}

		@Override
		protected Boolean doInBackground(Object... params) {

			try {
				DefaultHttpClient client = new DefaultHttpClient();
				String url = "http://parlezvous.herokuapp.com/message/" + username +"/" 
						+ password + "/" + addMessage.replace(" ", "%20");
				HttpGet request = new HttpGet(url);

				client.execute(request);

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return true;
		}

		@Override
		protected void onPostExecute(Object result) {

			pbMessages.setVisibility(View.INVISIBLE);
			etAddMessage.setText("");
			
			if (update == null || update.getStatus() == Status.FINISHED) {
				update = new UpdateMessages();
			}
			if (update.getStatus() == Status.PENDING)
				update.execute();

		}
	}
	
	/******************************************************************************************************/
	/** AsynTask pour l'historique des messages par utilisateur                                          **/
	/******************************************************************************************************/
	private class Historique extends AsyncTask {

		@Override
		protected void onPreExecute() {
			pbMessages.setVisibility(View.VISIBLE);
		}

		@Override
		protected Boolean doInBackground(Object... params) {

			return true;
		}

		@Override
		protected void onPostExecute(Object result) {
			pbMessages.setVisibility(View.INVISIBLE);
		}
	}
}
