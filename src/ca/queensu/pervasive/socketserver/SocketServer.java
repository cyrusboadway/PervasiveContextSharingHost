/**
 * <p>This is the runnable code of the pervasive computing
 * back end. It provides a multi-threaded (thread-safe)
 * socket interface, with which a web service can interact.</p>
 * 
 * <p>The reason for this design decision was to ensure a
 * single data source for the many client connections that
 * may occur to a database. If 100 clients query the database
 * simultaneously, this code will queue and execute each
 * query in turn. This makes a lot of the tricky db locking
 * issues irrelevant.</p>
 * 
 *  @author Cyrus Boadway
 * 
 */

package ca.queensu.pervasive.socketserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.UUID;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import ca.queensu.crypto.Key;
import ca.queensu.pervasive.repository.SynchronizedRepository;
import ca.queensu.zeroconf.ZeroconfListener;

public class SocketServer implements Runnable{

	//socket object
	private ServerSocket serverSocket;
	
	//port number
	private int port;
	
	//repository object to be passed to each worker thread.
	private SynchronizedRepository repository;

	
	/**
	 * Creates a socket server.
	 * 
	 * @param port: numeric port to which the socket server should bind.
	 * @throws IOException
	 */
	private void createSocket(int port){
		boolean portfound=false;

		//Try the first five ports from the suggested port, to find one that's open.
		for(int i=0;i<5 && !portfound; i++){
			try {
				//Create new socket.
				serverSocket = new ServerSocket(port+i);
				
				//Flag success.
				portfound=true;
			} catch (IOException e) {

				//Port already taken.
				System.err.println("Could not listen on port: "+(port+i)+".");
			}
		}
		
		//Complete and utter failure. SO MANY SERVERS ON THIS MACHINE!
		if(!portfound) System.exit(-1);

	}
	
	/**
	 * Another constructor, that accepts a non-blank repository.
	 * 
	 * @param port
	 * @param repository
	 * @throws IOException
	 */
	public SocketServer(int port, SynchronizedRepository repository) throws IOException{
		
		this.port = port;
		this.repository = repository;

		//This creates a runtime showdown hook that closes the current socket if the application is closed.
		Runtime.getRuntime().addShutdownHook(
			new Thread() {
				public void run() {
					shutdown();
				}
			}
		);

	}

	/**
	 * This is a shutdown hook that kills the serverSocket since it doesn't get killed on its own.
	 */
	private void shutdown(){
		try {
			
			//If it's not closed, close it.
			if(!this.serverSocket.isClosed())
				this.serverSocket.close();
			
		} catch (IOException e) {
			System.err.println("Couldn't close socket connection.");
			
		} finally{
			System.out.println("Server is down, one way or another.");
		}
	}

	/**
	 * Create a socket server locally (i.e. no bluetooth)
	 * 
	 * @param args n.a.
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException{
		//Create database instance.
		String name = UUID.randomUUID().toString();
		
		//Generate server key.
		Key pk = new Key();
		
		//New repo, to pass to non-static server object.
		SynchronizedRepository repo = new SynchronizedRepository(null, null, name, pk.getPrivateKey(), pk.getPublicKey());

		//Start new guy.
		SocketServer a = new SocketServer(4440, repo);
		
		a.run();
	}

	public void run() {

		createSocket(port);

		System.out.println("Server "+serverSocket.getInetAddress().toString()+":"+serverSocket.getLocalPort()+" is up.");
		
		this.repository.setAddress(new InetSocketAddress(serverSocket.getInetAddress(),serverSocket.getLocalPort()));
		
		//Register service with zeroconf
		try {
			this.repository.jmdns=JmDNS.create();
			ServiceInfo info = ServiceInfo.create("_pervasive._tcp.local.", repository.getHostID(), serverSocket.getLocalPort(), 0, 0, "");

			//Listen for changes to the zeroconf environment.
			repository.jmdns.addServiceListener("_pervasive._tcp.local.", new ZeroconfListener());

			repository.jmdns.registerService(info);
		} catch (IOException e) {
			System.err.println("Failed to create jmdns entry.");
			e.printStackTrace();
		}

		System.out.println("Host service registered, waiting.");

		while (true)
			try {
				new ServerWorker(serverSocket.accept(), repository).start();
			} catch (IOException e) {
				System.err.println("Could not accept socket connection. Server failed.");
				e.printStackTrace();
			}
				
	}

}