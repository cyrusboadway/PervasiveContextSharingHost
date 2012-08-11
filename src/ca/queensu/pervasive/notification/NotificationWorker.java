/**
 * 
 * This thread is spawned when a new notification needs to be sent out.
 * 
 * @author Cyrus Boadway
 * @date 2010-03-24
 * 
 */

package ca.queensu.pervasive.notification;

import java.net.InetSocketAddress;

import ca.queensu.pervasive.client.Client;
import ca.queensu.pervasive.common.NotificationStatement;
import ca.queensu.pervasive.repository.SynchronizedRepository;

public class NotificationWorker implements Runnable{

	private InetSocketAddress addr;

	private String queryHash;

	private SynchronizedRepository repository;
	
	/**
	 * Constructor
	 * 
	 * @param address	address to which the notification will be sent.
	 * @param queryHash	hash of the query being replied to.
	 * @param repository	The repository being used: necessary for dropping bad notification sets.
	 */
	public NotificationWorker(InetSocketAddress address, String queryHash, SynchronizedRepository repository){
		this.addr = address;
		this.queryHash = queryHash;
		this.repository = repository;
	}

	public void run() {
		
		NotificationStatement statement = new NotificationStatement(this.queryHash);
		
		try {
			//Send out notification, because stuff has changed.
			System.out.println("[Notification] Send notification message.");
			
			Client.sendMessage(statement, this.addr);
			
		} catch (Exception e) {
			
			//If the damned thing times out, just ditch the subscription all together.
			repository.removeNotification(this.addr);
			
		}
	}
	
}
