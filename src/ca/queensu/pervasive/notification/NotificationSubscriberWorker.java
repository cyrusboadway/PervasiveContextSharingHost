package ca.queensu.pervasive.notification;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import ca.queensu.pervasive.common.NotificationStatement;

public class NotificationSubscriberWorker extends Thread{

	private Socket socket;
	
	private NotificationCallback callback;
	
	public NotificationSubscriberWorker(Socket socket, NotificationCallback callback){
		this.socket = socket;
		this.callback = callback;
	}
	
	public void run(){
		
		try{
			//Create application socket streams.
			ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
			
			//Read notification object.
			NotificationStatement obj = (NotificationStatement) is.readObject();
			
			//Message response.
			System.out.println("Notified for query:"+obj.getQueryHash());
			
			//Do something.
			callback.eventCallback();
			
			//Write thank you response.
			os.writeObject("Thanks");
			
			//Shut it down, Lemon. Shut it down.
			os.close();
			is.close();
			socket.close();
			
		} catch (IOException e){
			
		} catch (ClassNotFoundException e) {
			// Couldn't read incomming message.
			e.printStackTrace();
		}
		
	}
	
}