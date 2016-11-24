package assignment7;

import java.util.Observable;

public class Conversation extends Observable{
	private String name;

	public Conversation(String chatName){
		name = chatName;
	}

	public void send(String msg){
		setChanged();
		notifyObservers(name + " " + msg);
	}

	public String getName(){
		return name;
	}
}
