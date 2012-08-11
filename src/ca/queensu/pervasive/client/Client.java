package ca.queensu.pervasive.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import java.net.UnknownHostException;

//import javax.jmdns.JmDNS;
//import javax.jmdns.ServiceInfo;

public class Client {

	private static Object messageing(Object requestObject, Socket socket) throws Exception{
		Object result = null;
		try{	

			ObjectOutputStream objectWriter = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream objectReader = new ObjectInputStream(socket.getInputStream());

			objectWriter.writeObject(requestObject);
			objectWriter.flush();

			result = objectReader.readObject();
			socket.close();

			return result;
		} catch (UnknownHostException e) {
			throw new Exception();
		} catch (IOException e) {
			throw new Exception();
		} catch (ClassNotFoundException e) {
			throw new Exception();
		}
	}
	
	public static Object sendMessage(Object requestObject, InetSocketAddress addr) throws Exception{
		Socket socket = new Socket();
		try {
			socket.connect(addr, 1000);
			return messageing(requestObject, socket);
		} catch (IOException e) {
			throw new Exception();
		}
	}
	
	/**
	 * @deprecated
	 * @param requestObject
	 * @param host String domain name of host
	 * @param port the numeric port to which the client should connect
	 * @return a response object.
	 * @throws Exception
	 */
	public static Object sendMessage(Object requestObject, String host, int port) throws Exception{
		return sendMessage(requestObject, new InetSocketAddress(host,port));
	}
}
