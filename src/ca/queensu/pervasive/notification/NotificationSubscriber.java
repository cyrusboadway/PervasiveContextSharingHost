/**
 * This class listens for notification messages and evokes some kind of response when they do happen.
 * 
 * @author Cyrus Boadway
 * @date 2010-03-24
 * 
 */

package ca.queensu.pervasive.notification;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import ca.queensu.crypto.Codec;
import ca.queensu.crypto.Key;
import ca.queensu.pervasive.client.Client;
import ca.queensu.pervasive.common.HostKeyRequest;
import ca.queensu.pervasive.common.HostKeyResponse;
import ca.queensu.pervasive.common.LetterOfMark;
import ca.queensu.pervasive.common.NotificationRequest;
import ca.queensu.pervasive.common.NotificationResponse;

public class NotificationSubscriber implements Runnable{

	private int port;
	
	private NotificationCallback callback;
	
	public NotificationSubscriber(int port, NotificationCallback callback){
		this.port = port;
		this.callback = callback;
	}
	
	/**
	 * This method allows any client to easily subscribe to a host for changes to the host's local situation.
	 * 
	 * @param query	Query about which changes in status will generate notifications.
	 * @param host	The address of the host that will provide the notifications.
	 * @param subscriber	The socket address of the notification subscription client.
	 * @param hostKey	The key of the host to whom the request is being made.
	 * @param letterOfMark	Letter of mark for the subscription host.
	 * @throws Exception	If the request is denie
	 */	
	public static void RequestSubscription(String query, InetSocketAddress host, InetSocketAddress subscriber, String hostKey, LetterOfMark letterOfMark) throws Exception{
		
		//Create a notification object.
		NotificationRequest req = new NotificationRequest(subscriber, query);
		
		//Set the request's letter of mark.
		req.setLetterOfMark(letterOfMark);
		
		System.out.println("Trying to create new subscription.");
		
		//Try the request.
		NotificationResponse res = (NotificationResponse)Client.sendMessage(req, host);
		
		System.out.println("NEW SUBSCRIPTION.");
		
		//If the query failed, throw an exception.
		if (res.error()>0) throw new Exception();

	}
	
	@Override
	public void run() {
		try {
			
			//Create a socket server on the given port.
			ServerSocket serverSocket = new ServerSocket(port);
			
			//Accept connections.
			while(true)
				new NotificationSubscriberWorker(serverSocket.accept(), callback).start();

		} catch (IOException e) {
			System.err.println("Couldn't create socket server.");
		}
	}

	/**
	 * For testing, this main method gives a way to see if the subscription service is working by generating a notification endpoint.
	 * 
	 * @param args
	 * @throws Exception 
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception{
		Thread t = new Thread(new NotificationSubscriber(4500, null));
		t.start();
		
		Key k = new Key();
		
		HostKeyResponse hkres = (HostKeyResponse) Client.sendMessage(new HostKeyRequest(), "localhost", 4440);
		
		LetterOfMark lom = new LetterOfMark(k.getPublicKey(), hkres.key(), 1299286379);
		
		lom.setSignature(Codec.sign(lom.fingerprint(), k.getPrivateKey()));
	
		String query = "declare namespace base=\"http://cs.queensu.ca/ContextBase\"; " +
						"declare namespace person=\"http://cs.queensu.ca/PersonBase\"; \n" +
						"declare namespace device=\"http://cs.queensu.ca/DeviceBase\"; \n" +
						"for $status in //base:Entity/device:featureSet[@type=\"device:telephoneFeatureSet\"][not(device:ringVolume=\"silent\")]/device:status \n" +
						"where not($status=\"waiting\") \n" +
						"return count($status)";
		
		NotificationRequest req = new NotificationRequest(new InetSocketAddress("localhost",4500), query);
		
		req.setLetterOfMark(lom);
		
		Client.sendMessage(req, "localhost", 4440);
	}

}