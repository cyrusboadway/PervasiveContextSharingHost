package run;
import java.io.IOException;

import ca.queensu.crypto.Key;
import ca.queensu.pervasive.bluetoothserver.BluetoothServer;
import ca.queensu.pervasive.repository.SynchronizedRepository;
import ca.queensu.pervasive.socketserver.SocketServer;

public class run {

	public static void main(String[] args) throws IOException{
		
		//Create blank repository
		String name = java.util.UUID.randomUUID().toString();
		Key pk = new Key();
		SynchronizedRepository repo = new SynchronizedRepository(null, null, name, pk.getPrivateKey(), pk.getPublicKey());
		
		System.out.println("* Creating endpoints");
		//Create server endpoints
		SocketServer soc = new SocketServer(4440, repo);
		BluetoothServer bt = new BluetoothServer(repo);
		
		
		System.out.println("* Starting up endpoints.");
		//run server endpoints
		Thread bluetoothThread = new Thread(bt);
		Thread tcpThread = new Thread(soc);
		bluetoothThread.start();
		tcpThread.start();

	}

}