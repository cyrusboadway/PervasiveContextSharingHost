package test;

import java.net.InetSocketAddress;

import ca.queensu.pervasive.client.Client;
import ca.queensu.pervasive.common.HostKeyRequest;
import ca.queensu.pervasive.common.HostKeyResponse;

/**
 * This method is really a placeholder. There has to be some kind of mechanism to
 * verify the host key received, either by having a signing list, or some centralized
 * repository of keys, with authenticated exchange. Right now, there is a man-in-the-middle
 * vulnerability.
 *  
 * @author cyrus
 *
 */

public class GetHostKeyTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		System.out.println("Requestion host key.");

		HostKeyRequest req = new HostKeyRequest();

		HostKeyResponse res = (HostKeyResponse) Client.sendMessage(req,new InetSocketAddress("localhost",4440));

		System.out.println(res.key());
		
		System.out.println("Reply received.");
		
	}

}
