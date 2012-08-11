/**
 * Cyrus Boadway - May 4th, 2009.
 * 
 * A Letter of Mark (LOM) gives permission for one entity to act on
 * behalf of another within a Situation.
 * 
 * This class allows a client to sign the key of a host, enabling the
 * host to make queries to other hosts with the client's authorization
 * and level of permission. A timestamp is also assigned to the LOM,
 * giving the LOM a lifespan that should periodically be renewed.
 */

package ca.queensu.pervasive.common;

import java.io.Serializable;
import java.security.MessageDigest;

public class LetterOfMark implements Serializable{

	private static final long serialVersionUID = 7400486507185681663L;

	private String clientPublicKey;
	
	private long timestamp;
	
	private String hostkey;
	
	private String signature; 
	
	/**
	 * Constructor.
	 * 
	 * @param clientid - ID of the client doing the enabling.
	 * @param hostid - ID of the enabled host.
	 * @param hostkey - public key of the enabled host.
	 * @param expiry - UNIX timestamp of the expiry of the LOM.
	 */
	public LetterOfMark(String clientPublicKey, String hostPublicKey, long expiry){
		this.timestamp = expiry;
		this.hostkey = hostPublicKey;
		this.signature = null;
		this.clientPublicKey = clientPublicKey;
	}
	
	/**
	 * Accessor method for the expiry of this letter of mark.
	 * @return A UNIX timestamp of this LOM's expiry.
	 */
	public long expiry(){
		return this.timestamp;
	}
	
	/**
	 * Accessor method for the correct host.
	 * @return the public key of the enabled host.
	 */
	public String hostKey(){
		return this.hostkey;
	}
	
	/**
	 * Accessor method for the client's public key. 
	 * @return the public key of the authorizing client.
	 */
	public String clientKey(){
		return this.clientPublicKey;
	}
	
	/**
	 * Accessor method for signature;
	 * @return the LOM's signature, signed by the associated client.
	 */
	public String signature(){
		return this.signature;
	}
	
	/**
	 * Add a signature to the object if it hasn't already been signed. Signature should be of this object's 'fingerprint()'. 
	 * @param signature - signature to be added to the LOM.
	 */
	public void setSignature(String signature){
		this.signature = signature;
	}
	
	/**
	 * Provides an equals method, to comply with proper implementation of Serializable.
	 * 
	 * @param lom - another LOM to which this LOM should be compared.
	 * @return - true if their content is equal, false otherwise.
	 */
	public boolean equals(LetterOfMark lom){
		return (lom.timestamp == this.timestamp && lom.hostkey.equals(this.hostkey) && lom.signature.equals(this.signature));
	}
	
	/**
	 * This method provides a finger print of its contents, the output of which is signed by the client.
	 * @return
	 */
	public byte[] fingerprint(){
		MessageDigest sha1;
		try {
			sha1 = MessageDigest.getInstance("SHA1");
		} catch (java.security.NoSuchAlgorithmException e) {
			return null;
		}
		return sha1.digest((timestamp+hostkey).getBytes());
	}
	
}