/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other
 * contributors as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.labs.datagrid.lab4;

import java.util.List;
import java.util.Properties;

import org.apache.lucene.search.Query;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.infinispan.AdvancedCache;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.query.Search;
import org.infinispan.query.SearchManager;
import org.infinispan.transaction.LockingMode;

/**
 * 
 * 
 * @author Romain Pelisse
 *
 */
public class LoadMoviesAndSearch implements Runnable {

	public static final String ACTOR_SEARCHED = "Sylvester Stallone";
	public static final String ACTOR_IGNORED = "Arnold Schwarzenegger";

	public static final String INDEXED_FIELD_NAME = "name";
	public static final String KEY_PREFIX = "key";

	
	public static final String CACHE_NAME = "indexedCache";

	private final String nodeName;
	
	public LoadMoviesAndSearch() {
		nodeName = System.getProperty("user.name");
		cacheManager = createCacheManagerProgramatically();
	}
	
	private EmbeddedCacheManager createCacheManagerProgramatically() {
		return new DefaultCacheManager(buildGlobalConfiguration(),
							buildCacheConfiguration());
	}

	private GlobalConfiguration buildGlobalConfiguration() {
		return GlobalConfigurationBuilder
				.defaultClusteredBuilder().transport()
				.clusterName(System.getenv("HOSTNAME")).machineId(nodeName).rackId("local").defaultTransport()
				.globalJmxStatistics().enable()
				.allowDuplicateDomains(true)
				.cacheManagerName(CACHE_NAME).build();
	}

	private static Configuration buildCacheConfiguration() {
		 Properties properties = new Properties();
		 // FIXME: 2) Switch directory provider to file system - observe the impact on performances
		 properties.put("hibernate.search.default.directory_provider", "infinispan");
		 properties.put("hibernate.search.lucene_version", "LUCENE_36");
		 properties.put("hibernate.search.default.indexwriter.use_compound_file", "false");
         properties.put("default.indexmanager", "near-real-time");
         
		 return new ConfigurationBuilder()
		 .clustering().cacheMode(CacheMode.DIST_SYNC).hash().numOwners(1)
		 .invocationBatching().enable().transaction().syncCommitPhase(true).lockingMode(LockingMode.PESSIMISTIC)
		 .indexing().enable()
		 .indexLocalOnly(true)
		 .withProperties(properties)
		 .build();
	}

	private final EmbeddedCacheManager cacheManager;

	protected EmbeddedCacheManager getCacheManager() {
		return cacheManager;
	}

	protected String getNodeName() {
		return nodeName;
	}

	
	public static void main(String[] args) throws Exception {		
		new LoadMoviesAndSearch().run();
	}

	public void run() {
		System.out.println("Starting Node " +  getNodeName() + ".");
		importWithIndexingOn(getCacheManager());
		System.exit(0);
	}

	private void importWithIndexingOn(EmbeddedCacheManager cacheManager) {
		final long NB_MOVIES = 300L;

		AdvancedCache<Object, Object> cache = cacheManager.getCache(CACHE_NAME).getAdvancedCache();
		System.out.print("Import data... ");		
		loadData(cache, NB_MOVIES);
	    searchValueByIndexedField(cache,INDEXED_FIELD_NAME, ACTOR_SEARCHED, Movie.class);
	}

	private void loadData(Cache<Object,Object> c, final long chunkSize) {

		try {
			long startTime = System.currentTimeMillis();
			
			c.startBatch();
			for ( long i = 0 ; i < chunkSize ; i++ )
				c.put(KEY_PREFIX + i, new Movie(i, randomString()));
			c.endBatch(true);

			System.out.println("Import took:" + (System.currentTimeMillis() - startTime) + "ms");
		} catch ( Exception e) {
			throw new IllegalStateException(e);
		}
	}

	
	public static void pause(long nbSeconds) {
		long second = 1000L;
		System.out.print("Pause for " + nbSeconds + ", ");
		for ( long countdown = nbSeconds; countdown > 0 ; countdown--) {
			try {
				Thread.sleep(second);
				System.out.print(countdown + " ");
			} catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
		}
		System.out.println("... Done.");
	}

	public static <T> void searchValueByIndexedField(Cache<Object,Object> c, String indexedFieldName, String indexedValue, Class<T> clazz) {
		SearchManager searchManager = Search.getSearchManager(c);
		QueryBuilder builder = searchManager.buildQueryBuilderForClass(clazz).get();
		Query query = builder.bool()
				.must(builder.keyword().onField(indexedFieldName)
				.matching(indexedValue).createQuery())
				.createQuery(); 
		// FIXME: 1) modified search to returns not exact matches, but items that contains the value
		System.out.println("Query:" + query);
		final long startTime = System.currentTimeMillis();
		List<Object> results = searchManager.getQuery(query, clazz).list();
		System.out.println("Results from query:" + results.size() + " items, query took: " + 
				(System.currentTimeMillis() - startTime) + "ms.");
		for ( Object o :  results ) {
			System.out.println(o);
		}
	}
	
	private int threshold = 0;
	private String randomString() {
		if ( threshold == 100 ) {
			threshold = 0;
			return ACTOR_SEARCHED;
		}
		threshold++;
		return ACTOR_IGNORED;
	}

}
