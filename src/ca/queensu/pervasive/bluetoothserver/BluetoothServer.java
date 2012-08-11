/**
 * 
 * This lovely bit of code acts as a Bluetooth socket server. It's pretty straightforward;
 * it creates a bluetooth endpoint, and passes the socket connection created onto a
 * worker thread from the standard network socket suite. 
 * 
 * @author Cyrus Boadway
 * @date 2010
 * 
 */

package ca.queensu.pervasive.bluetoothserver;

import java.io.IOException;

import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import ca.queensu.crypto.Key;
import ca.queensu.pervasive.repository.SynchronizedRepository;
import ca.queensu.pervasive.socketserver.ServerWorker;

public class BluetoothServer implements Runnable{

	//Each server class maintains a link to the shared repository. It passes the objects on to the server workers.
	private SynchronizedRepository repository;
	
	//Construct
	public BluetoothServer(SynchronizedRepository repository){
		this.repository = repository;
	}
	
	public void run() {
		try {
			//build uri sting, including, pubic key digest. The UUID actually also defines the protocol.
			String url = "btspp://localhost:" + new UUID( 0x1101 ).toString() + ";name=ContextHost-"+this.repository.getHostID().replace('.', '_').replace('/','-')+";";

			//create bt endpoint
			StreamConnectionNotifier service = (StreamConnectionNotifier) Connector.open( url );

			System.out.println("Bluetooth server running: " + url);
			
			//Push this into the repository object (so everyone knows there's a bt endpoint.
			repository.setbtspp(url);

			//Catch new incomming connections and spawn generic socket serverworkers.
			while (true){
				StreamConnection con = (StreamConnection) service.acceptAndOpen();
				
				try {
					//spawn worker thread.
					new ServerWorker(con.openInputStream(),con.openOutputStream(), this.repository).start();
					
				} catch (IOException e) {
					System.err.println("Could not accept socket connection. Server failed.");
					e.printStackTrace();
				}
			}
		} catch ( IOException e ) {
			System.err.print(e.toString());
		}
	}

	/**
	 * If you want a generic bt server, with a random key and blank repository, this class can be started from main().
	 * 
	 * @param args n.a.
	 */
	public static void main(String[] args){
		//Create database instance.
		String name = java.util.UUID.randomUUID().toString();
		
		//Create a random new key.
		Key pk = new Key();
		
		//Create repo, with no network server connected.
		SynchronizedRepository repo = new SynchronizedRepository(null, null, name, pk.getPrivateKey(), pk.getPublicKey());
		
		//Create and run this server object.
		BluetoothServer server = new BluetoothServer(repo);

		server.run();
	}

}