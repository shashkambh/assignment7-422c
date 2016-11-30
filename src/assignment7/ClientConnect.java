/* Chat Client - Project 7
 * Shashank Kambhampati
 * skk834
 * 16445
 * Slip days used: 0
 * Fall 2016
 */
package assignment7;

import java.net.*;
import java.io.*;
import javafx.application.Platform;

public class ClientConnect{
	private BufferedReader in;
	private PrintWriter out;
	private boolean running;
	private ClientMain view;
	private Socket sock;
	private String name;

	public ClientConnect(ClientMain gui, String ip){
		view = gui;
		
		try{
			sock = new Socket(ip, 4242);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintWriter(sock.getOutputStream());
			running = true;
			name = in.readLine();
			new Thread(()->listen()).start();
		} catch(Exception e){
			System.out.println("ERROR");
			e.printStackTrace();
		}
	}
	
	public void sendToServer(String message){
		out.println("server " + message);
		out.flush();
	}

	public void send(String message){
		if(!message.trim().equals("")){
			out.println(view.getCurrentChat() + " " + message);
			out.flush();
		}
	}

	private void listen(){
		System.out.println("Listening");
		try{
			String input = in.readLine();

			while(running){
				System.out.println(input);

				if(!input.contains(" ")){
					final String convo = input;
					Platform.runLater(() -> view.addConversation(convo));
				} else {
					String[] fromServer = input.split(" ", 3);
					final String msg;
					if(fromServer[1].equals(name + ":")){
						msg = "me: " + fromServer[2];
					} else if(fromServer[0].equals("server") && fromServer[1].equals("login")){
						name = fromServer[2];
						msg = "Name successfully changed to " + name;
					} else {
						msg = fromServer[1] + " " + fromServer[2];
					}
					
					Platform.runLater(() -> view.appendToConversation(fromServer[0], msg));
				}
				
				input = in.readLine();
			}
		} catch(IOException ex){
			System.out.println("Connection lost.");
		}
	}

	public void close(){
		running = false;
		out.println("exit");
		out.flush();
		try{
			sock.close();
		} catch(IOException e){
			
		}
	}

	public String getName(){
		return name;
	}
}
