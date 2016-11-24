package assignment7;

import java.net.*;
import java.util.*;

public class ServerMain{
	private static List<Conversation> conversations;
	private static List<Client> users;
	
	public static void main(String[] args){
		conversations = new ArrayList<>();
		users = new ArrayList<>();
		
		try{
			ServerSocket serverSock = new ServerSocket(4242);
			System.out.println("Listening");
			while(true){
				Client newUser = new Client(serverSock.accept());
				users.add(newUser);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void message(Client sender, String msg){
		System.out.println(msg);
		
		String[] message = msg.split(" ", 2);

		if(message[0].equals("server")){
			sender.send("server " + sender.getName() + ": " + message[1]);
			String[] command = message[1].split(" ");
			if(command.length >= 2 && command[0].equals("createChat")){
				boolean found = false;

				for(Conversation e : conversations){
					if(e.getName().equals(command[1])){
						found = true;
						sender.send("server Room name already taken");
					}
				}

				if(!found){
					Conversation newThread = new Conversation(command[1]);
					newThread.addObserver(sender);
					conversations.add(newThread);
					sender.send(command[1]);
				}

			} else if(command.length >= 3 && command[0].equals("createLogin")){
				boolean found = false;

				for(Client e : users){
					if(e.getName().equals(command[1])){
						found = true;
						sender.send("server Username already taken");
					}
				}

				if(!found){
					sender.createLogin(command[1], command[2]);
				}

			} else if(command.length >= 3 && command[0].equals("addToChat")){

				Client req = null;
				for(Client toAdd : users){
					if(toAdd.getName().equals(command[2])){
						req = toAdd;
					}
				}
				
				if(req != null){
					for(Conversation e : conversations){
						if(e.getName().equals(command[1])){
							e.addObserver(req);
							req.send(e.getName());
							e.send(req.getName() + " has been added to the chat.");
						}
					}
				}
			}
		}
		
		for(Conversation e : conversations){
			if(e.getName().equals(message[0])){
				e.send(sender.getName() + ": " + message[1]);
				break;
			}
		}
	}

}
