/**
 * This is a notification response object, that lets the notification client know that the
 * response has been accepted, and the hash that uniquely identifies the notification relationship. 
 * 
 * @author Cyrus Boadway
 * @date 2010-3-22
 * 
 */

package ca.queensu.pervasive.common;

import java.io.Serializable;

public class NotificationResponse extends Response implements Serializable{

	public static final int ERROR_BAD_SUBSCRIPTION_INPUTS = 3;
	
	private static final long serialVersionUID = 8809974525351663004L;
	private String hash; 

	/**
	 * 
	 * Construct the response body.
	 * 
	 * @param queryHash - A SHA1 hash of the query string (Base64 encoded).
	 * 
	 */
	public NotificationResponse(String queryHash){
		super();

		this.hash = queryHash;
	}

	/**
	 * 
	 * Accessor method for the hash value of the notification contract.
	 * 
	 * @return - A SHA1 hash value in a string.
	 */
	
	public String getQueryHash(){
		return this.hash;
	}

}