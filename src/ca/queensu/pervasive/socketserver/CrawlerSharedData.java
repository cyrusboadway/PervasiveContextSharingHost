package ca.queensu.pervasive.socketserver;
import java.util.Vector;

import ca.queensu.mutex.Semaphore;
import ca.queensu.pervasive.common.QueryResponse;

public class CrawlerSharedData {

	private int crawlers;
	private Vector<QueryResponse> results;
	public Semaphore semaphore;
	private boolean verbose=false;
	
	public CrawlerSharedData(int crawlers){
		results = new Vector<QueryResponse>();
		this.crawlers = crawlers;
		semaphore = new Semaphore(0);
	}
	
	public synchronized void add(QueryResponse result){
		if(verbose) System.out.println("Result added");
		results.add(result);
		dec();
	}
	
	public synchronized void abort(){
		dec();
	}
	
	private synchronized void dec(){
		crawlers--;
		if(verbose) System.out.println("crawler decremented to "+crawlers+".");
		if(crawlers<=0) semaphore.Vsignal();
		
	}
	
	public synchronized Vector<QueryResponse> retrieve(){
		return this.results;
	}
}
