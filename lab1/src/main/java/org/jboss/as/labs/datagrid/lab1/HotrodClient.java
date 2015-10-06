package org.jboss.as.labs.datagrid.lab1;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.api.BasicCache;
import org.infinispan.commons.api.BasicCacheContainer;

public class HotrodClient {

	public static void main(String[] args) {
		ConfigurationBuilder cb	= new ConfigurationBuilder();
		cb.tcpNoDelay(true).connectionPool()
		      .numTestsPerEvictionRun(3)
		      .testOnBorrow(false)
		      .testOnReturn(false)
		      .testWhileIdle(true)
		      .addServer()
		      .host("localhost")
		      .port(11222);
		
        BasicCacheContainer cacheContainer = new RemoteCacheManager(cb.build());
        BasicCache<String, String> cache = cacheContainer.getCache(); //FIXME: set a cache name and configure it to have a eviction of 100000

        // FIXME: change the code below to import 100000 items, then requests all the available keys - how many are there in the cache ?
        cache.put("car", "ferrari");
        assert cache.get("car").equals("ferrari");
        cache.remove("car");
        assert !cache.containsKey("car") : "Value must have been removed!";
	}
}
