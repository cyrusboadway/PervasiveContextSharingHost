package ca.queensu.pervasive.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.UnknownHostException;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

public class BluetoothClient {


	/**
	 * 
	 * Send a pervasive message to a Bluetooth Host and return the response object.
	 * 
	 * @param requestObject - any object really, but an error is thrown if it's not a pervasive message.
	 * @param btURI - URI of the socket connection to query.
	 * @return - a pervasive response message.
	 * @throws Exception the message was not delivered.
	 */
	public static Object sendMessage(Object requestObject, String btURI) throws Exception{
		Object result = null;
		try {
			StreamConnection con = (StreamConnection) Connector.open(btURI);

			try{	

				ObjectOutputStream objectWriter = new ObjectOutputStream(con.openDataOutputStream());
				ObjectInputStream objectReader = new ObjectInputStream(con.openInputStream());

				objectWriter.writeObject(requestObject);
				objectWriter.flush();
				result = objectReader.readObject();
				con.close();

				return result;
			} catch (UnknownHostException e) {
				throw new Exception();
			} catch (IOException e) {
				throw new Exception();
			} catch (ClassNotFoundException e) {
				throw new Exception();
			}


		} catch (IOException e) {
			System.err.println("failed to connect to bluetooth host");
			e.printStackTrace();
		}

		return result;
	}
}
