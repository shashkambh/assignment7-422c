package assignment7;

import java.util.*;
import java.io.*;
import java.net.*;

public class Client implements Observer{
	private static int id = 0;
	
	private PrintWriter out;
	private BufferedReader in;
	private String name;
	private String password;
	
	public Client(Socket conn){
		id++;
		try{
			out = new PrintWriter(conn.getOutputStream());
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			name = "guest" + id;
			out.println(name);
			out.flush();
			new Thread(()->listen()).start();
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void update(Observable obv, Object outStr){
		send(outStr);
	}

	private void listen(){
		try{
			String input = in.readLine();

			while(!input.equalsIgnoreCase("exit")){
				ServerMain.message(this, input);
				input = in.readLine();
			}
		} catch(IOException ex){
			System.out.println("Connection lost.");
		}
	}

	public void createLogin(String username, String passwd){
		name = username;
		password = passwd;
	}

	public String getName(){
		return name;
	}

	public void send(Object msg){
		out.println(msg);
		out.flush();
	}
}
