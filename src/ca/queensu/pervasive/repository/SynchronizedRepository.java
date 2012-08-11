package ca.queensu.pervasive.repository;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Vector;

import javax.jmdns.JmDNS;
import org.basex.query.QueryException;

import ca.queensu.crypto.Codec;
import ca.queensu.pervasive.common.LetterOfMark;
import ca.queensu.pervasive.xquery.XQuery;
import ca.queensu.xml.XMLHandler;
//import ca.queensu.crypto.Codec;

public class SynchronizedRepository {

	private String hostid;

	private InetSocketAddress address;

	private String publicKey;

	private String privateKey;

	private HashMap<String, LetterOfMark> lettersOfMark;

	private Hashtable<String, String> clients;

	public JmDNS jmdns;

	public String btspp;
	
	public Vector<NotificationSubscriptionData> notifications; 
	
	/**
	 * 
	 * @param address Socket address of the host maintaining this repository. Set to null if none present.
	 * @param btspp Address for the bluetooth server device. null, if none present.
	 * @param hostid ID of the host maintaining this repository.
	 * @param privateKey Base64 encoded RSA Private key of the host maintaining this repository.
	 * @param publicKey Base64 encoded RSA Public key of the host maintaining this repository.
	 */
	public SynchronizedRepository(InetSocketAddress address, String btspp, String hostid, String privateKey, String publicKey) {
		this.hostid = hostid;
		this.hostid = Codec.sha1(publicKey.getBytes());
		this.privateKey = privateKey;
		this.publicKey = publicKey;
		this.lettersOfMark = new HashMap<String, LetterOfMark>();
		this.address = address;
		this.btspp = btspp;
		this.notifications = new Vector<NotificationSubscriptionData>();

		//Unnecessary to synchronize in the constructor, but I'm doing it anyway.
		synchronized(this){
			this.clients = new Hashtable<String, String>();	
		}
	}

	/**
	 * 
	 * @return The socket address of the host maintaining this repository.
	 */
	public InetSocketAddress getAddress(){
		return this.address;
	}

	/**
	 * @return Returns the host id of the server.
	 */
	public String getHostID(){
		return this.hostid;
	}

	/**
	 * 
	 * @param address The socket address of the host maintaining this repository.
	 */
	public void setAddress(InetSocketAddress address){
		this.address = address;
	}

	public String getbtspp(){
		return this.btspp;
	}

	public void setbtspp(String btspp){
		this.btspp = btspp;
	}

	/**
	 * Checks a letter of mark's signature against a the user's known public key.
	 * 
	 * @param letterOfMark - Object to be verified.
	 * @param publicKey - key against which to verify to object's signature.
	 * @return - true if the LOM's signature matches the public key.
	 */
	public boolean validateLetterOfMark(LetterOfMark letterOfMark, String publicKey) {
		// TODO: Validate Letter of Mark.

		// NOTE: May be null, if the client is looking for a public view, but
		// should return false!
		if (letterOfMark==null) return false;
		if(letterOfMark.fingerprint()==null || letterOfMark.signature()==null || publicKey==null) return false;
		else if(!Codec.verifySignature(letterOfMark.fingerprint(), letterOfMark.signature(), publicKey))
			return false;
		return true;
	}

	/**
	 * Add a client to the repository.
	 * 
	 * @param clientXML
	 * @param lom
	 */
	public boolean addClient(String clientXML, LetterOfMark letterOfMark) {
		synchronized(this){


			//Validating a letter of mark against itself seems silly, but it's really just checking that the lom will validate with other hosts for the given public key.
			if(letterOfMark==null) return false;
			if (!validateLetterOfMark(letterOfMark,letterOfMark.clientKey())){
				System.err.println("LetterOfMark ain't no good!");
				return false;
			}

			//TODO: No parsing happening: hook it up to the schema files...
			//Validate XML.
			if(!XMLHandler.SAXParserCheck(clientXML)){
				System.err.println("XML didn't validate.");
				return false;
			}

			//Store new entries.
			lettersOfMark.put(Codec.sha1(letterOfMark.clientKey().getBytes()), letterOfMark);
			clients.put(Codec.sha1(letterOfMark.clientKey().getBytes()), clientXML);

			System.out.println(this.clients.size()+" clients in db.");
			//Return success.
			return true;
		}
	}

	/**
	 * This method returns the complete unfiltered view of the repository.
	 * 
	 * @return - an XML string containing all of the clients registered with
	 *         this host.
	 * @throws QueryException 
	 */
	@SuppressWarnings("unchecked")
	public String getHost() throws QueryException {

		synchronized(this){

			//Host header xml.
			String host = "<host:Host id=\"" + XQuery.escapeString(hostid)
				+"\" xmlns:host=\"http://cs.queensu.ca/Host\" "
				+"schemaLocation=\"http://cs.queensu.ca/Host Host.xsd\" >\n"
				+"<host:key>"+publicKey+"</host:key>";

			Hashtable<String, String> temp = new Hashtable(clients);

			// Iterate through all clients, adding them to the host's xml.
			Iterator iter = temp.keySet().iterator();

			//for each entry in the db...
			while (iter.hasNext()) {

				//Keep the hash key.
				Object key = iter.next();

				//Get key value.
				String entity = temp.get(key);

				//Default query, should be replaced with the expired check query, but if the file read fails, will just pass through.
				String xquery = "/";

				//Get entity expiry query from file, and replace the template value.
				try {
					xquery = XMLHandler.readFile("./src/xqueries/checkEntityExpiry.xq").replace("ENTITY_EXPIRY", ""+(System.currentTimeMillis()/1000));
				} catch (Exception e) {
					//No biggie, keep going.
					System.err.println("Couldn't read query file.");
				}

				//Check that the entity is still "fresh"
				try{
					//run query
					String expiryTest = XQuery.query(entity, xquery);

					//If query returns expired, drop the entity from the repository, else add to host file.
					if(expiryTest.trim().equals("expired"))
						clients.remove(key);

					else if( expiryTest.trim().equals("valid"))							
						host += entity;

				}catch(QueryException e){
					//In the event of a fail, the entity is omitted from the Host's reply.
					System.err.println("Entity clean failed.");
				}
			}


			//Return host result + xml footer.
			return (host + "</host:Host>");
		}
	}

	/**
	 * Returns this host's private key.
	 * 
	 * @return - Base64 encoded RSA private key.
	 */
	public String getPrivateKey() {
		synchronized(this){
			return this.privateKey;
		}
	}

	/**
	 * Returns this host's public key.
	 * 
	 * @return - Base64 encoded RSA public key.
	 */
	public String getPublicKey() {
		synchronized(this){
			return this.publicKey;
		}
	}

	/**
	 * 
	 * Add a new Notification to query.
	 * 
	 * @param query
	 * @param clientSocket
	 * @throws Exception If there is something wrong with the inputs, the entry won't be added to the db.
	 */
	public void addNotification(String query, InetSocketAddress clientSocket, String clientKey) throws Exception{
		synchronized(this){
			
			System.out.println("[Subscription] New subscription:"+query);
			
			if(clientKey==null || query.length()<1 || query == null || clientSocket ==null)
				throw new Exception();
			
			NotificationSubscriptionData pair = new NotificationSubscriptionData(clientSocket, query, clientKey);
			
			//TODO: Make sure that the pair isn't already in there somewhere.
			
			this.notifications.add(pair);
		}
	}
	
	/**
	 * Steps through the notifications list and removes any items that have that socket address.
	 * 
	 * @param address
	 */
	public void removeNotification(InetSocketAddress address){
		synchronized(this){
			ListIterator<NotificationSubscriptionData> iter = this.notifications.listIterator();
			
			//Check each entity and remove those that reference that socket address.
			while(iter.hasNext())
				if(iter.next().socket.equals(address))
					iter.remove();

		}
	}
	
	/**
	 * Get the notifications vector.
	 * 
	 * @return Vector of Notification stuff.
	 */
	public Vector<NotificationSubscriptionData> getNotifications(){
		synchronized(this){
			return this.notifications;
		}
	}
}