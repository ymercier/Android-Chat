package fr.emn.android.chat.resource;

public class Message {
	
	private String auteur;
	private String contenu;
	
	public Message(String sAuteur, String sContenu)
	{
		auteur = sAuteur;
		contenu = sContenu;
	}
	
	public String getAuteur() {
		return auteur;
	}
	
	public void setAuteur(String auteur) {
		this.auteur = auteur;
	}
	
	public String getContenu() {
		return contenu;
	}
	
	public void setContenu(String contenu) {
		this.contenu = contenu;
	}
	
	
}
