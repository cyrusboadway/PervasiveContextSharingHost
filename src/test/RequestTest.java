package test;

import java.net.InetSocketAddress;

import ca.queensu.pervasive.client.Client;
import ca.queensu.pervasive.common.Request;

public class RequestTest {

	public static void main(String[] args) throws Exception {
		System.out.println("Sending message.");

		Request req = new Request();

		Client.sendMessage(req,new InetSocketAddress("localhost",4440));

		System.out.println("Reply received.");

	}
}