package org.jboss.as.labs.datagrid.lab1;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.api.BasicCache;
import org.infinispan.commons.api.BasicCacheContainer;

public class HotrodClient {

	private static final String SERVER_NAME = "localhost";
	private static final int SERVER_PORT = 11222;
	
	public static void main(String[] args) {
		ConfigurationBuilder cb	= new ConfigurationBuilder();
		cb.tcpNoDelay(true).connectionPool()
		      .numTestsPerEvictionRun(3)
		      .testOnBorrow(false)
		      .testOnReturn(false)
		      .testWhileIdle(true)
		      .addServer()
		      .host(SERVER_NAME)
		      .port(SERVER_PORT);
		
        BasicCacheContainer cacheContainer = new RemoteCacheManager(cb.build());
        BasicCache<String, String> cache = cacheContainer.getCache(); //FIXME: set a cache name and configure it to have a eviction of 100000

        final String key = "car";
        String value = "ferrari";
        // FIXME: change the code below to import 100000 items, then requests all the available keys - how many are there in the cache ?
        cache.put(key, value);
        assert cache.get(key).equals(value);
        System.out.println("Stored value for '" + key + "' is :" + cache.get(key));
        
        cache.remove("car");
        assert !cache.containsKey("car") : "Value must have been removed!";
        System.exit(0);
        
	}
}
