import java.net.*;
import java.io.*;
import javafx.application.Platform;

public class ClientConnect{
	private BufferedReader in;
	private PrintWriter out;
	private boolean running;
	private ClientMain view;

	public ClientConnect(ClientMain gui){
		view = gui;
		
		try{
			Socket sock = new Socket("127.0.0.1", 4242);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintWriter(sock.getOutputStream());
			running = true;
			new Thread(()->listen()).start();
		} catch(Exception e){
			System.out.println("ERROR");
			e.printStackTrace();
		}
	}
	
	public void sendToServer(String message){
		out.println(view.getCurrentChat() + " " + message);
		out.flush();
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
					String[] fromServer = input.split(" ", 2);
					Platform.runLater(() -> view.appendToConversation(fromServer[0], fromServer[1]));
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
	}
}
