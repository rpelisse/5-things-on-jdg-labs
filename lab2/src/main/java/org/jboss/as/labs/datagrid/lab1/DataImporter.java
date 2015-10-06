package org.jboss.as.labs.datagrid.lab1;

import java.util.Scanner;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.api.BasicCache;
import org.infinispan.commons.api.BasicCacheContainer;

public class DataImporter {

	private static final String SERVER_HOSTNAME = "localhost";
	private static final int SERVER_PORT = 11222;
	
	
	public static void main(String[] args) {
		
		ConfigurationBuilder cb	= new ConfigurationBuilder();
		cb.tcpNoDelay(true).connectionPool()
		      .numTestsPerEvictionRun(3)
		      .testOnBorrow(false)
		      .testOnReturn(false)
		      .testWhileIdle(true)
		      .addServer()
		      .host(SERVER_HOSTNAME)
		      .port(SERVER_PORT);
		
        BasicCacheContainer cacheContainer = new RemoteCacheManager(cb.build());
        BasicCache<String, String> cache = cacheContainer.getCache(); // FIXME: configure a 'lab3' cache with no eviction and an expiration of 60 minutes.

        final String key = System.getProperty("user.name");
        final String value = System.getenv("HOSTNAME");
        
        cache.put(key,value);
        System.out.println("If you laptop number is pair (2,4,6,...), please shutdown your instance. Otherwise just press any key to retrieved your value");
        while ( true ) {
	        System.out.print(">");
	        Scanner scanner = new Scanner(System.in);
	        // FIXME: modify to use the input as a key for a cache lookup
	        if ( scanner.next().equalsIgnoreCase("exit") ) {
	        	scanner.close();
	        	System.exit(0);
	        }
	        System.out.println(cache.get(key));
	        // FIXME: to test the distribute setting, change the code above to print ALL available values
        }
	}
}
