package fr.emn.chat.android.utilitaires;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import fr.emn.android.chat.resource.Message;

public class MessageParser {

	public static List<Message> parser(String serverContent) {

		List<Message> messages = new ArrayList<Message>();

		String delimsMessage = "[;]";
		String delimsUserText = "[:]";

		String[] chatMessages = serverContent.split(delimsMessage);

		for (int i = 0; i < chatMessages.length; i++) {
			Log.i("MessageParser", chatMessages[i]);

			String[] autorAndContent = chatMessages[i].split(delimsUserText);

			if(autorAndContent.length == 2){
				if(!autorAndContent[1].trim().isEmpty()){
					Message message = new Message(autorAndContent[0], autorAndContent[1]);
					messages.add(message);
				}
			}
		}

		return messages;
	}
}
