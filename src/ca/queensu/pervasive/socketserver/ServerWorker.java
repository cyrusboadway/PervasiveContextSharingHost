/**
 * <p>This is a thread class, spawned for every socket
 * connection to the database. It handles, in a thread-
 * safe fashion, access to the database object. This is
 * important, given that there is constant rewriting of
 * the data object, depending on the query made. </p>
 * 
 * 
 * @author cyrus boadway
 * @date March 28th, 2009
 * 
 */

package ca.queensu.pervasive.socketserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ListIterator;
import java.util.Vector;

import ca.queensu.crypto.Codec;
import ca.queensu.crypto.CodecException;
import ca.queensu.pervasive.common.ClientRegistrationRequest;
import ca.queensu.pervasive.common.ClientRegistrationResponse;
import ca.queensu.pervasive.common.HostKeyRequest;
import ca.queensu.pervasive.common.HostKeyResponse;
import ca.queensu.pervasive.common.LetterOfMark;
import ca.queensu.pervasive.common.NotificationRequest;
import ca.queensu.pervasive.common.NotificationResponse;
import ca.queensu.pervasive.common.QueryRequest;
import ca.queensu.pervasive.common.QueryResponse;
import ca.queensu.pervasive.common.Response;
import ca.queensu.pervasive.notification.NotificationWorker;
import ca.queensu.pervasive.repository.NotificationSubscriptionData;
import ca.queensu.pervasive.repository.SynchronizedRepository;
import ca.queensu.pervasive.xquery.XQuery;

import javax.jmdns.ServiceInfo;
import org.basex.query.QueryException;

public class ServerWorker extends Thread {
//	private Socket socket = null;

	private InputStream inputStream = null;
	private OutputStream outputStream = null;
	private Socket socket = null;
	
	private SynchronizedRepository repository;

	/**
	 * The worker constructor method sets up the static
	 * repository and accepts the incoming socket connection. This one is pretty much depricated. 
	 * 
	 * @param socket
	 * @param repository
	 */	
	public ServerWorker(InputStream inputStream, OutputStream outputStream, SynchronizedRepository repository){
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		this.repository = repository;
		this.socket = null;
	}
	
	/**
	 * This is a constructor that accepts a socket connection as its socket source. The socket
	 * can come from anywhere, like a network connection or a bluetooth connection.
	 * 
	 * @param socket
	 * @param repository
	 * @throws IOException
	 */
	public ServerWorker(Socket socket, SynchronizedRepository repository) throws IOException {
		super("ServerWorker");

		this.inputStream = socket.getInputStream();
		this.outputStream = socket.getOutputStream();
		this.repository = repository;
		
		this.socket = socket;
	}

	private boolean verifyInputs(LetterOfMark lom){
		if (lom==null) return false;
		if(lom.clientKey()==null || lom.hostKey()==null) return false;
		if(lom.clientKey().length()==0 || lom.hostKey().length()==0) return false;

		return Codec.verifySignature(lom.fingerprint(), lom.signature(), lom.clientKey());
	}

	private QueryResponse handleGeneralQuery(QueryRequest request){

		//branch to different query types (Local vs. Global).
		if(request.scope()==QueryRequest.LOCAL) return handleLocalQuery(request);
		else if(request.scope()==QueryRequest.GLOBAL) return handleGlobalQuery(request);
		
		QueryResponse qres=new QueryResponse();
		qres.setError(QueryResponse.ERROR_COULD_NOT_COMPUTE);
		return qres;
	}

	private QueryResponse handleGlobalQuery(QueryRequest request){
		QueryResponse qres = new QueryResponse();
		
		//Verify letter of mark.
		if(!verifyInputs(request.letterOfMark()) || request.letterOfMark().hostKey()==repository.getPublicKey()){
			System.err.println("Bad Letter of Mark");
			qres.setError(QueryResponse.ERROR_AUTHENTICATION);
			qres.setResponse("");
			return qres;
		}
		
		//Get a list of available hosts through the zeroconf interface & count them.
		ServiceInfo[] infos = repository.jmdns.list("_pervasive._tcp.local.");
		int foreignHosts = 0;
		for(ServiceInfo info : infos) //Check that none in list are *this* host.
			if(!(repository.jmdns.getHostName().equals(info.getServer()) && info.getPort() == repository.getAddress().getPort()))
				foreignHosts++;

		//Create Crawler shared data object.
		CrawlerSharedData crawlerData = new CrawlerSharedData(foreignHosts);

		//Create Request object.
		QueryRequest crawlerQuery = new QueryRequest();
		crawlerQuery.setQuery("/");
		crawlerQuery.setClientKey(repository.getPublicKey());
		crawlerQuery.setLetterOfMark(request.letterOfMark());
		crawlerQuery.setScope(QueryRequest.LOCAL);
		for(ServiceInfo info : infos){
			if(!(repository.jmdns.getHostName().equals(info.getServer()) && info.getPort() == repository.getAddress().getPort())){
				System.out.println("FOREIGN:"+info.getServer()+":"+info.getPort());
				new Crawler(new InetSocketAddress(info.getServer(),info.getPort()),crawlerQuery,crawlerData).start();
			} else{
				System.out.println("LOCAL:"+info.getServer()+":"+info.getPort());
			}
		}

		//Wait for crawlers to come home (if there are any).
		if(foreignHosts>0)crawlerData.semaphore.Pwait();

		//Collect all responses into a Vector.
		Vector<QueryResponse> responses = crawlerData.retrieve();
		responses.add(handleLocalQuery(crawlerQuery));	//add the localhost view.
		

		String view = "<situation>";
		
		for (int i=0;i<responses.size();i++){
			QueryResponse response = responses.get(i);
			System.out.println("MESSAGE "+i+": ");
			System.out.println("Error:"+response.error());
			try {
				if(response.error()!=QueryResponse.ERROR_NONE)
					System.err.println("Skipping result because the query failed.");
				else if(response.encrypted)
					view+=Codec.decrypt(response.response, repository.getPrivateKey());
				else view+=responses.get(i).response;
			} catch (CodecException e) {
				System.err.println("Skipping result because it wasn't encoded right.");
			}
		}
		view+="</situation>";
		
		try {

			//view = XQuery.identity(view);	//Clean view (correct xml markup)
			
			qres.response = XQuery.query(view, request.query());
			qres.setError(0);
		} catch (QueryException e) {
			qres.response = "";
			qres.setError(QueryResponse.ERROR_AUTHENTICATION);
		}
		return qres;

	}
	
	/**
	 * 
	 * Responds with this host's view queried.
	 * 
	 * @param request
	 * @return XML string of this host's view, queried.
	 */
	private QueryResponse handleLocalQuery(QueryRequest request){
		
		//Blank response & result string.
		QueryResponse qres = new QueryResponse(); 
		String result = "";

		//Check we're working with good inputs.
		if(!verifyInputs(request.letterOfMark())){
			qres.setError(QueryResponse.ERROR_AUTHENTICATION);
		}

		try {

			//Get the base XML.
			String xml = repository.getHost();
			//Drop private nodes.
			String view = XQuery.getView(xml, Codec.sha1(request.letterOfMark().clientKey().getBytes()));
			//Perform query.
			result = XQuery.query(view, request.query());
			//Encrypt result.
			try {
				if(request.clientKey()!=null){
					String encrypted = Codec.encrypt(result, request.letterOfMark().clientKey());
					result = encrypted;
					qres.encrypted = true;
					qres.setError(QueryResponse.ERROR_NONE);
				}
			} catch (CodecException e) {
				qres.setError(QueryResponse.ERROR_AUTHENTICATION);
				return qres;
			}
		} catch (QueryException e) {
			qres.setError(QueryResponse.ERROR_COULD_NOT_COMPUTE);
			return qres;
		}
		
		//return result upon success.
		qres.setResponse(result);
		return qres;

	}

	/**
	 * Handles a HostKeyRequest, getting the host's key, enveloping it, and returning it.
	 * 
	 * @param request
	 * @return HostKeyResponse, containing this host's public RSA key. 
	 */
	private HostKeyResponse handleHostKey(HostKeyRequest request){
		return new HostKeyResponse(this.repository.getPublicKey());
	}

	private ClientRegistrationResponse handleClientRegistration(ClientRegistrationRequest request){
		ClientRegistrationResponse res = new ClientRegistrationResponse();
		try {
			//Try and add the new client.
			if(repository.addClient(Codec.decrypt(request.clientXML(), repository.getPrivateKey()), request.issuedLetterOfMark())){
				
				//Check to see if any notifications need to be updated.
				checkNotificationChanges();
				
				return res;
			}
		} catch (CodecException e) {
			//Turns out the message wasn't meant for us!
			res.setError(ClientRegistrationResponse.ERROR_AUTHENTICATION);
			return res;
		}

		//If the data received is invalid, return an error.
		res.setError(ClientRegistrationResponse.ERROR_COULD_NOT_COMPUTE);
		return res;
	}

	
	/**
	 * 
	 * This method checks each of the notification pairs and spawns responses if there are responses to be made.
	 * 
	 */
	private void checkNotificationChanges() {
		
		//Cache the threads to be issued.
		Vector<Thread> notificationThreads = new Vector<Thread>();
		
		//Get the host's content before entering the synchronized business.
		String hostXML;
		try {
			hostXML = this.repository.getHost();
		} catch (QueryException e1) {
			//Couldn't use the host? BADBADBAD.
			return;
		}
		
		synchronized(this.repository){
			
			//Walk through each notification, and check if a flag needs to be sent out to any notification clients.
			ListIterator<NotificationSubscriptionData> iter = repository.notifications.listIterator();
			
			while(iter.hasNext()){
				
				NotificationSubscriptionData pair = (NotificationSubscriptionData) iter.next(); 
				
				try {
					//Check the current state.
					String currentState = XQuery.getView(hostXML, Codec.sha1(pair.clientKey.getBytes()));
					
					currentState = XQuery.query(currentState, pair.query);
					
					//Hash the state out.
					String currentHash = Codec.sha1(currentState.getBytes());
					
					if(!currentHash.equals(pair.lastQueryHash)){
					
						//Update notification pair object and push back into the list.
						pair.lastQueryHash = currentHash;
						iter.set(pair);
						
						//Create notification thread.
						notificationThreads.add(new Thread(new NotificationWorker(pair.socket, pair.query, repository)));
						
					}
					
				} catch (QueryException e) {
					//Query failed. Remove? Nah. That would mean I trusted the query engine.
					//TODO: Change this so that after a certain number of failed attempts it gets dropped.
				}
				
			}
		}
		
		//Start each of the threads. Starting these outside of the synchronised section seems smart.
		for(int i=0; i<notificationThreads.size(); i++)
			notificationThreads.get(i).start();
		
	}

	/**
	 * This method returns the object type, numerically encoded. Useful for the later switch statement.
	 * 
	 * @param request
	 * @return integer indicating message class type.
	 */
	private int getClass(Object request){
		if(request.getClass().getName().equals("ca.queensu.pervasive.common.Request"))
			return 1;
		if(request.getClass().getName().equals("ca.queensu.pervasive.common.QueryRequest"))
			return 2;
		if(request.getClass().getName().equals("ca.queensu.pervasive.common.HostKeyRequest"))
			return 3;
		if(request.getClass().getName().equals("ca.queensu.pervasive.common.ClientRegistrationRequest"))
			return 4;
		if(request.getClass().getName().equals("ca.queensu.pervasive.common.NotificationRequest"))
			return 5;
		
		return 0;
	}

	/**
	 * The threaded entry point is here.
	 */
	public void run() {	
		try {
			
			
			//Get object input streams from socket connection.
			ObjectInputStream objectReader = new ObjectInputStream(inputStream);
			ObjectOutputStream objectWriter = new ObjectOutputStream(outputStream);

			//Get object.
			Object request =  objectReader.readObject();
			Object response = null;

			//Respond to request type.
			switch(getClass(request)){
			case 1:
				response = new Response();
				break;
			case 2:
				response = handleGeneralQuery((QueryRequest) request);
				break;
			case 3:
				response = handleHostKey((HostKeyRequest) request);
				break;
			case 4:
				response = handleClientRegistration((ClientRegistrationRequest) request);
				break;
			case 5:
				response = registerNewNotificationSubscription((NotificationRequest) request);
				break;
			default:
				Response r = new Response();
				r.setError(Response.ERROR_COULD_NOT_COMPUTE);
				response = r;
				System.err.println("Unknown class:"+request.getClass().getName());
			}

			//Write response.
			objectWriter.writeObject(response);
			
			//Shut it down, Lemon. Shut it down.
			objectReader.close();
			objectWriter.close();
			inputStream.close();
			outputStream.close();
			if(socket!=null) socket.close();
			
			System.out.println("Message received; reply sent.");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e){

		} finally {
			
			
			
		}
	}

	/**
	 * This method adds a new notification handler.
	 * 
	 * @param request
	 * @return
	 */
	private Object registerNewNotificationSubscription(NotificationRequest request) {

		NotificationResponse response = new NotificationResponse(Codec.sha1(request.getQuery().getBytes()));
		
		try {
			this.repository.addNotification(request.getQuery(), request.getAddress(), request.letterOfMark.clientKey());
		} catch (Exception e) {
			response.setError(NotificationResponse.ERROR_BAD_SUBSCRIPTION_INPUTS);
		}
		
		return response;
	}
}