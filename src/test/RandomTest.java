package test;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Set;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceTypeListener;

import ca.queensu.crypto.*;

@SuppressWarnings("unused")
public class RandomTest {

    static class SampleListener implements ServiceTypeListener {
        
        public void serviceTypeAdded(ServiceEvent event) {
            System.out.println("Service type added: " +event.getType());
        }
    }
	
	public static void main(String[] args) {
		
		try {
            JmDNS jmdns = JmDNS.create();
            jmdns.registerServiceType("_pervasive._tcp.local.");
            
            while (true) {
                ServiceInfo[] infos = jmdns.list("_pervasive._tcp.local.");
                for (int i=0; i < infos.length; i++) {
                    System.out.println(infos[i]);
                }
                System.out.println();
                
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
