package org.jboss.as.labs.datagrid.lab5;

import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.remoting.transport.Address;


public class SimpleExecutionNode implements Runnable {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		new SimpleExecutionNode().run();
	}
	
	public void run() {
		EmbeddedCacheManager manager = InfinispanUtils.buildInfinispanConfiguration();
		manager.start();
		manager.getCache(); // just to "start" the service
		System.out.println("Cache Started:" + manager.getAddress());
		while ( true ) {
			System.out.print("Cluster Members:");
			for (Address add : manager.getMembers() )
				System.out.print(add + " ");
			InfinispanUtils.pause(10000L);
		}
	}
}
