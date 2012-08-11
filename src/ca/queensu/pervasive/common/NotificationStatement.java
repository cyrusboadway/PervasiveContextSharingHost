/**
 * 
 * This message notifies a Notification Subscriber that their has been a change in the
 * data they would like to be notified about.
 * 
 */

package ca.queensu.pervasive.common;

public class NotificationStatement extends Request{

	
	private static final long serialVersionUID = -930374504836159790L;
	
	private String queryHash;
	
	/**
	 * Create a Notification Statement.
	 * 
	 * @param queryHash
	 */
	public NotificationStatement(String queryHash){
		super();
		this.queryHash = queryHash;
	}
	
	/**
	 * 
	 * Accessor method for the hash for which this notification is being sent.
	 * 
	 * @return
	 */
	public String getQueryHash(){
		return this.queryHash;
	}
}
