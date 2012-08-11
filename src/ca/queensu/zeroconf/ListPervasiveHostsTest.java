//Licensed under Apache License version 2.0
//Original license LGPL

//%Z%%M%, %I%, %G%

//This library is free software; you can redistribute it and/or
//modify it under the terms of the GNU Lesser General Public
//License as published by the Free Software Foundation; either
//version 2.1 of the License, or (at your option) any later version.

//This library is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//Lesser General Public License for more details.

//You should have received a copy of the GNU Lesser General Public
//License along with this library; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

package ca.queensu.zeroconf;

import java.io.IOException;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

/**
 * Sample Code for Service Discovery using JmDNS and a ServiceListener.
 * <p>
 * Run the main method of this class. It listens for HTTP services and lists
 * all changes on System.out.
 *
 * @author  Werner Randelshofer
 * @version 	%I%, %G%
 */
public class ListPervasiveHostsTest {

	static class SampleListener implements ServiceListener {
		public void serviceAdded(ServiceEvent event) {
			System.out.println("Service added   : " + event.getName()+"."+event.getType());
		}
		public void serviceRemoved(ServiceEvent event) {
			System.out.println("Service removed : " + event.getName()+"."+event.getType());
		}
		public void serviceResolved(ServiceEvent event) {
			System.out.println("Service resolved: " + event.getInfo());
		}
	}
	/**
	 * @param args the command line arguments
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		try {
            JmDNS jmdns = JmDNS.create();
            while (true) {
                ServiceInfo[] infos = jmdns.list("_pervasive._tcp.local.");
                for (int i=0; i < infos.length; i++) {
                    //System.out.println(infos[i]);
                    System.out.println(infos[i].getHostAddress()+":"+infos[i].getPort());
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
