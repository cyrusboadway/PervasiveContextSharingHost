/**
 * 
 * This is a notifcation request object. A host will respond to the client when there
 * is a change in the local response to a query. 
 * 
 * @author Cyrus Boadway
 * @date 2010-03-22
 * 
 */

package ca.queensu.pervasive.common;

import java.io.Serializable;
import java.net.InetSocketAddress;

public class NotificationRequest extends Request implements Serializable{

	private static final long serialVersionUID = -298568544299567276L;

	private InetSocketAddress address;
	
	private String query;
	
	/**
	 * Create a notification request. A host will the respond for changes in the query until the client disappears.
	 * 
	 * @param address
	 * @param query
	 */
	public NotificationRequest(InetSocketAddress address, String query){
		super();
		
		this.address = address;
		this.query = query;
	}
	
	/**
	 * Accessor method for the query for which to be notified.
	 * 
	 * @return xquery string.
	 */
	public String getQuery(){
		return this.query;
	}
	
	/**
	 * Accessor method for the address of the notification client.
	 * 
	 * @return InetSocketAddress of the requesting user. (should be verified against the requesting uri)
	 */
	public InetSocketAddress getAddress(){
		return this.address;
	}
	
}
