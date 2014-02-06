package fr.emn.android.chat.adapter;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.emn.android.chat.R;
import fr.emn.android.chat.resource.Message;

public class ChatAdapter extends BaseAdapter {

	private List<Message> messages = Collections.EMPTY_LIST;
	private LayoutInflater inflater;
	
	public ChatAdapter(Context context,List<Message> lMessages) {
		inflater = LayoutInflater.from(context);
		this.messages = lMessages;
	}

	@Override
	public int getCount() {
		return messages.size();
	}

	@Override
	public Object getItem(int location) {
		return messages.get(location);
	}

	@Override
	public long getItemId(int location) {
		return location;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if(convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.message, null);

			holder.tvAuteur = (TextView) convertView.findViewById(R.id.tvauteur);
			holder.tvContenu = (TextView) convertView.findViewById(R.id.tvcontenu);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tvAuteur.setText(messages.get(position).getAuteur());
		holder.tvContenu.setText(messages.get(position).getContenu());

		return convertView;

	}
	
	
	private class ViewHolder {
		TextView tvAuteur;
		TextView tvContenu;
	}

}
