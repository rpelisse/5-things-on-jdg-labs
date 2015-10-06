package org.jboss.as.labs.datagrid.lab5;

import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public final class InfinispanUtils {

	private InfinispanUtils() {}
	
	public static EmbeddedCacheManager buildInfinispanEmbeddedConfiguration() {
		return (EmbeddedCacheManager) buildInfinispanConfiguration();
	}
	
	public static DefaultCacheManager buildInfinispanConfiguration() {
		GlobalConfiguration globalConfig = buildGlobalConfiguration();

		Configuration config = new ConfigurationBuilder().clustering()
				.cacheMode(CacheMode.DIST_SYNC).hash().build();
	
		DefaultCacheManager manager = new DefaultCacheManager(globalConfig, config);
		System.out.println(manager.getMembers());
		return manager;
	}
	
	private static GlobalConfiguration buildGlobalConfiguration() {
		try {
			return GlobalConfigurationBuilder
	                  .defaultClusteredBuilder().transport()
	                  .clusterName("LocalCluster").machineId("localhost").rackId("local").defaultTransport()
	                  .globalJmxStatistics().disable()
	                  .allowDuplicateDomains(true).cacheManagerName("manager").build();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		
	}
	
	public static void pause(long nbMilliseconds) {
		try {
			System.out.println("Pause for " + nbMilliseconds + "ms.");
			Thread.sleep(nbMilliseconds);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}
}
