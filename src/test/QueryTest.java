
package test;

import java.net.InetSocketAddress;

import ca.queensu.crypto.Codec;
import ca.queensu.crypto.Key;
import ca.queensu.pervasive.client.AwesomeClient;
import ca.queensu.pervasive.client.Client;
import ca.queensu.pervasive.common.HostKeyRequest;
import ca.queensu.pervasive.common.HostKeyResponse;
import ca.queensu.pervasive.common.LetterOfMark;
import ca.queensu.pervasive.common.QueryRequest;
import ca.queensu.pervasive.common.QueryResponse;

import ca.queensu.xml.XMLHandler;

@SuppressWarnings("unused")
public class QueryTest{
	
	private static void globalQuery() throws Exception{
		System.out.println("Sending message.");
		QueryRequest req = new QueryRequest();

		InetSocketAddress hostAddress = new InetSocketAddress("localhost",4440);

		
		String query =	"/";
		//query = "for $x in root() return $x";
		
		//Get email of person whose last name is Anderson
		//query = XMLHandler.readFile("xqueries/AndersonsEmail.xq");
		
		
		//Create some credentials (UUID, RSA keys)
		Key key = new Key();
		String publicKey = key.getPublicKey();
		String privateKey = key.getPrivateKey();

		//Keys for device1
		

		//Get the host key and create a letter of mark.
		HostKeyResponse hkres = (HostKeyResponse) Client.sendMessage(new HostKeyRequest(), hostAddress);
		//System.out.println(hkres.key());
		
		
		LetterOfMark lom = new LetterOfMark(publicKey, hkres.key(), 1990000000);
		lom.setSignature(Codec.sign(lom.fingerprint(), key.getPrivateKey()));

		//Create the request object.
		req.setClientKey(publicKey);
		req.setQuery(query);
		req.setScope(QueryRequest.LOCAL);	//make it a local/global query.
		req.setLetterOfMark(lom);

		QueryResponse response = (QueryResponse) Client.sendMessage(req,hostAddress);

		System.out.println("Reply received.");

		if(response.error()!=0)
			System.err.println("Query resulted in error.");
		
		if(response.encrypted){
			response.setResponse(Codec.decrypt(response.response, privateKey));
		}

		System.out.println(response.response);
	}
	
	private static void localQuery() throws Exception{
		System.out.println("Sending message.");
		QueryRequest req = new QueryRequest();

		InetSocketAddress hostAddress = new InetSocketAddress("localhost",4440);

		//Get devices owned by person1
		String query =	"/";
		//query = "for $x in root() return $x";
		//query = "/";
		query = XMLHandler.readFile("xqueries/AndersonsEmail.xq");
		

		//Create some credentials (UUID, RSA keys)
		Key key = new Key();
		String publicKey = key.getPublicKey();
		String privateKey = key.getPrivateKey();
		//Keys for device1
		publicKey = "C86VC0q62Ig6I8Rt3G410GK00u6D0321YGA1WG2cJubc8cplMcx/AAi5z94azc1nsHgqbxI.CuRdSMTLEk9r43z/gMWIYo9qrb40URb6I7LyqxZ4FLEkVg0Yd.41pcn6MlvRZQcoQoFijHY7VDHeLvoSAX2dRBzN5MKdpxtEPmTGlFp9ne/5Al1zujT31CRVyRcgOfyG9m18UkTHrm830G01";
		privateKey = "C882TG81030D1WagXaY6zmq10G4500I20bymWW9R0W400e610APFYMOYREzQRlyegmNqaIJsO77P6hINjBupXkTnPrKwudKGFt.fQ1AB8dJMKG1vkKP8TNpJkCGzKwv.e2AVuG7ER4PQ/bkDgR9h8.or68Tyr6XNd9mg4ATilrSLPIVFlSvd1r2y/Cd6ZyKgy7tYrqC4nj/nkQfYdn0d04Xwvr7N0WC10042WO1lEcoZKKpN./oMJ6MjKlShw5ToRGWELXFypKjXIHWQ2OhXJX.PolmUmiqbfHo1c.TCsJCoR0jxkwHMvBj6pFJNWahc8V/AeW1PR0wazGOycCD1hx16.CqQ/oSANy18ybav8h6N0TDMvEMTuB/DM974SmTk0Gf4jfzdmkswX/A7kG910E7k1x3FV8NnrilUE5dwis44MXFIZ.GarOho5PSDeY6w.9OTfgXEkc2.OVJ32tLCrxPT/8dDfxiWdjIUE2Ea8di2GG2ySYFwnpRGs84aZu6cJhNxbSQ3Nj9pMLcVJE72toyLH2rSZK63YUAGL2g3lwIIm2SOJOzJsXg/MARHv7jfjErL0a0GYNYyesDCzfUlKdEpUbiuvRMcByfYUwEvoE1rhmJ83KHsq7BBRF/DPm8AXM18LBYHMwt9T6ahb4QhxspIXmWJ0a0iUup.htF33BW/LnYrbpBXzbBhmy1DjZElfCXDvRq9UXHp6cpoZUFBIVBNCg.uR5zE7r3Bx5eF/Eypy8afHAeP0a1HYeNBjf/q.cquiwcUy5zC/OeRlIbR0F3KgC/BuMOk9KT9JsmwO9NNEnh8MyCxGhvscfurJzeLOKnsCLldXJlJ";

		
		LetterOfMark lom = new LetterOfMark(publicKey, publicKey, 1300000000);
		lom.setSignature(Codec.sign(lom.fingerprint(), key.getPrivateKey()));

		//Create the request object.
		req.setClientKey(publicKey);
		req.setQuery(query);
		req.setScope(QueryRequest.LOCAL);	//make it a local/global query.
		req.setLetterOfMark(lom);

		QueryResponse response = (QueryResponse) Client.sendMessage(req,hostAddress);

		System.out.println("Reply received.");

		if(response.encrypted){
			response.setResponse(Codec.decrypt(response.response, privateKey));
		}

		System.out.println("RESPONSE:"+response.response);
	}

	public static Key device1Key(){
		String publicKey = "C86VC0q62Ig6I8Rt3G410GK00u6D0321YGA1WG2cJubc8cplMcx/AAi5z94azc1nsHgqbxI.CuRdSMTLEk9r43z/gMWIYo9qrb40URb6I7LyqxZ4FLEkVg0Yd.41pcn6MlvRZQcoQoFijHY7VDHeLvoSAX2dRBzN5MKdpxtEPmTGlFp9ne/5Al1zujT31CRVyRcgOfyG9m18UkTHrm830G01";
		String privateKey = "C882TG81030D1WagXaY6zmq10G4500I20bymWW9R0W400e610APFYMOYREzQRlyegmNqaIJsO77P6hINjBupXkTnPrKwudKGFt.fQ1AB8dJMKG1vkKP8TNpJkCGzKwv.e2AVuG7ER4PQ/bkDgR9h8.or68Tyr6XNd9mg4ATilrSLPIVFlSvd1r2y/Cd6ZyKgy7tYrqC4nj/nkQfYdn0d04Xwvr7N0WC10042WO1lEcoZKKpN./oMJ6MjKlShw5ToRGWELXFypKjXIHWQ2OhXJX.PolmUmiqbfHo1c.TCsJCoR0jxkwHMvBj6pFJNWahc8V/AeW1PR0wazGOycCD1hx16.CqQ/oSANy18ybav8h6N0TDMvEMTuB/DM974SmTk0Gf4jfzdmkswX/A7kG910E7k1x3FV8NnrilUE5dwis44MXFIZ.GarOho5PSDeY6w.9OTfgXEkc2.OVJ32tLCrxPT/8dDfxiWdjIUE2Ea8di2GG2ySYFwnpRGs84aZu6cJhNxbSQ3Nj9pMLcVJE72toyLH2rSZK63YUAGL2g3lwIIm2SOJOzJsXg/MARHv7jfjErL0a0GYNYyesDCzfUlKdEpUbiuvRMcByfYUwEvoE1rhmJ83KHsq7BBRF/DPm8AXM18LBYHMwt9T6ahb4QhxspIXmWJ0a0iUup.htF33BW/LnYrbpBXzbBhmy1DjZElfCXDvRq9UXHp6cpoZUFBIVBNCg.uR5zE7r3Bx5eF/Eypy8afHAeP0a1HYeNBjf/q.cquiwcUy5zC/OeRlIbR0F3KgC/BuMOk9KT9JsmwO9NNEnh8MyCxGhvscfurJzeLOKnsCLldXJlJ";
		Key key = new Key(publicKey,privateKey);
		return key;
	}
	
	public static void main(String[] args) throws Exception {
		//globalQuery();
		InetSocketAddress serverAddress = new InetSocketAddress("localhost",4440);
		//Key key = device1Key();
		
		globalQuery();
		
		//String hostKey = AwesomeClient.getHostKey(serverAddress, key);
		
		//String result = AwesomeClient.QueryHost(serverAddress, hostKey, key, "/", System.currentTimeMillis()/1000+60, AwesomeClient.GLOBAL);
		
		//System.out.println(result);
	}

}