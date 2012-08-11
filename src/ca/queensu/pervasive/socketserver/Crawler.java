package ca.queensu.pervasive.socketserver;

import java.net.InetSocketAddress;

import ca.queensu.pervasive.client.Client;
import ca.queensu.pervasive.common.QueryRequest;
import ca.queensu.pervasive.common.QueryResponse;

public class Crawler extends Thread{
	
	private InetSocketAddress socketAddress;
	private QueryRequest request;
	private CrawlerSharedData dataObject;
	
	public Crawler(InetSocketAddress socketAddress, QueryRequest request, CrawlerSharedData dataObject){
		this.socketAddress = socketAddress;
		this.request = request;
		this.dataObject = dataObject;
	}
	
	public void run(){
		try {
			QueryResponse answer = (QueryResponse) Client.sendMessage(request, socketAddress);
			dataObject.add(answer);
		} catch (Exception e) {
			dataObject.abort();
		}
	}
}
