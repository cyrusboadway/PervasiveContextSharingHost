/**
 * A class for holding the data related to a notification subscription.
 * 
 * @author Cyrus Boadway
 *
 */

package ca.queensu.pervasive.repository;

import java.net.InetSocketAddress;


public class NotificationSubscriptionData{
	public InetSocketAddress socket;
	public String query;
	public String lastQueryHash;
	public String clientKey;
	
	NotificationSubscriptionData(InetSocketAddress socket, String query, String clientKey){
		this.lastQueryHash = null;
		this.query = query;
		this.socket = socket;
		this.clientKey = clientKey;
		
	}
}
