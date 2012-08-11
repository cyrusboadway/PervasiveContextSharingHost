package ca.queensu.zeroconf;

import javax.jmdns.ServiceEvent;

import javax.jmdns.ServiceListener;

/**
 * Class to handle updates to the zeroconf universe.
 * 
 * @author cyrus
 *
 */
public class ZeroconfListener implements ServiceListener {

	public ZeroconfListener(){
		
	}

	public void serviceAdded(ServiceEvent event) {
		System.out.println("Service added   : " + event.getName()+"."+event.getType());
		//repo.setHostAddresses(getHosts());
	}
	public void serviceRemoved(ServiceEvent event) {
		System.out.println("Service removed : " + event.getName()+"."+event.getType());
		//repo.setHostAddresses(getHosts());
	}
	public void serviceResolved(ServiceEvent event) {
		System.out.println("Service resolved: " + event.getInfo());
		//repo.setHosts(getHosts());
	}
}