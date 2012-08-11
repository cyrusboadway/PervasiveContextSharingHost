package ca.queensu.pervasive.client;

import java.net.InetSocketAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import ca.queensu.crypto.Codec;
import ca.queensu.crypto.Key;
import ca.queensu.pervasive.common.ClientRegistrationRequest;
import ca.queensu.pervasive.common.ClientRegistrationResponse;
import ca.queensu.pervasive.common.HostKeyRequest;
import ca.queensu.pervasive.common.HostKeyResponse;
import ca.queensu.pervasive.common.LetterOfMark;
import ca.queensu.pervasive.common.QueryRequest;
import ca.queensu.pervasive.common.QueryResponse;

public class AwesomeClient extends Client {

	public static int GLOBAL = QueryRequest.GLOBAL;
	public static int LOCAL = QueryRequest.LOCAL;
	
	
 	public static String getHostKey(InetSocketAddress serverAddress, Key key) throws Exception{
		//Prepare letter of mark
		LetterOfMark lom = new LetterOfMark(key.getPublicKey(), "", System.currentTimeMillis()/1000+60);
		
		
		HostKeyRequest hkreq = new HostKeyRequest();
		
		hkreq.setLetterOfMark(lom);
		HostKeyResponse hkres = (HostKeyResponse)sendMessage(hkreq, serverAddress);
		
		if(hkres.error()!=0) throw new Exception();
		
		String message = hkres.key();

		if(hkres.encrypted)
			message = Codec.decrypt(hkres.key(), key.getPrivateKey());		
		
		return message;
	}
	
	public static String QueryHost(InetSocketAddress serverAddress, String hostKey, Key key, String query, long letterOfMarkExpiry, int queryScope) throws Exception{
		
		String decodeKey = key.getPublicKey();
		if(queryScope==AwesomeClient.GLOBAL) decodeKey = hostKey;
		
		LetterOfMark letterOfMark = generateCompleteLetterOfMark(key, decodeKey, queryScope);
		
		QueryRequest req = new QueryRequest();
		
		req.setLetterOfMark(letterOfMark);
		req.setClientKey(key.getPublicKey());
		req.setQuery(query);
		req.setScope(queryScope);
		
		QueryResponse qres = (QueryResponse)sendMessage(req, serverAddress);
		
		if(qres.error()!=0) throw new Exception();
		String message = qres.response();
		if(qres.encrypted) message = Codec.decrypt(message, key.getPrivateKey());
		return message;
	}
	
	public static InetSocketAddress findHost(JmDNS jmdns) throws Exception{
		ServiceInfo[] infos = jmdns.list("_pervasive._tcp.local.");

		if(infos==null) return null;
		//If there are no hosts, return nothing.
		
		System.out.println("HOST COUNT"+infos.length);
		if(infos.length==0) return null;

		//Pick fist host in list
		return new InetSocketAddress(infos[0].getInetAddress(),infos[0].getPort());

	}

	public static LetterOfMark generateCompleteLetterOfMark(Key key, String hostKey, long expiry){
				
		LetterOfMark lom = new LetterOfMark(key.getPublicKey(), hostKey, expiry);
		lom.setSignature(Codec.sign(lom.fingerprint(), key.getPrivateKey()));
		return lom;
	}
	
	public static void registerEntity(InetSocketAddress serverAddress, String hostKey, Key clientKey, String validatedXML, long expiry) throws Exception{

		validatedXML = Codec.encrypt(validatedXML, hostKey);
		
		LetterOfMark lom = generateCompleteLetterOfMark(clientKey, clientKey.getPublicKey(), expiry);

		ClientRegistrationRequest req = new ClientRegistrationRequest(validatedXML,lom);
		ClientRegistrationResponse res = (ClientRegistrationResponse)Client.sendMessage(req, serverAddress);
		if(!res.success())
			throw new Exception();

	}
}
