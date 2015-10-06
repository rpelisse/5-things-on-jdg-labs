package org.jboss.as.labs.datagrid.lab5;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.infinispan.Cache;
import org.infinispan.distexec.DefaultExecutorService;
import org.infinispan.distexec.DistributedExecutorService;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.remoting.transport.Address;

public class DispatcherNode implements Runnable {

	public static void main(String[] args) {
		new DispatcherNode().run();
	}
	
	public void run() {
		EmbeddedCacheManager manager = InfinispanUtils.buildInfinispanConfiguration();
		DistributedExecutorService des = new DefaultExecutorService(manager.getCache());
		InfinispanUtils.pause(10000L);
		// Load data into the grid
		for ( int i = 0 ; i < 10000 ; i++ ) 
			manager.getCache().put("key" + i, "value" + i);
		// First usage, dispatch task to node
		runTasksOverTheGridWithRoundRobin(10, manager, des);
		// Second usage, dispatch task to some node
		runTasksOverTheGridWithRoundRobin(des);		
		// Third usage, dispatch task to ALL nodes
		runTaskOverAllNodes(des);
		// Fourth usage, uses a cache to feed data to the task, using to balance the load
		Cache<Integer,String> cache = manager.getCache();
		runTaskOnDataOwningNode(des, cache);
		// FIXME: Implements a Map/Reduce algorythm selecting all value finishing by 5 and returning the value with the "value" prefix
	}

	private static void runTaskOnDataOwningNode(DistributedExecutorService des, Cache<Integer,String> cache) {
		addDataToCache(cache);
		InfinispanUtils.pause(10000L);
		for ( Object key : cache.keySet() )
			printResults(des.submitEverywhere(new InfinispanDistributableCallable(), key));
	}

	private static void runTasksOverTheGridWithRoundRobin(DistributedExecutorService des) {
		printResult(des.submit(new RegularCallable("I've been run somewhere, by some node (but only once)...")));
	}
	
	private static void addDataToCache(Cache<Integer, String> cache) {
		System.out.print("Load data in the cache...");
		final int NB_ITEMS_IN_CACHE = 1000;
		for ( int i = 0; i < NB_ITEMS_IN_CACHE; i++ )
			cache.put(new Integer(i), "Item nb" + i);
		final int offset = NB_ITEMS_IN_CACHE * 100;
		for ( int i = 0; i < NB_ITEMS_IN_CACHE; i++ )
			cache.put(new Integer(i + offset), "Item nb" + i);
		System.out.println("Done.");
	}

	private static void runTaskOverAllNodes(DistributedExecutorService des) {
		InfinispanUtils.pause(10000L);
		printResults(des.submitEverywhere(new RegularCallable("Ran on every node...")));
	}

	private static void runTasksOverTheGridWithRoundRobin(int nbTaskToRun, EmbeddedCacheManager manager, DistributedExecutorService des) {
		while (nbTaskToRun > 0 )
			for ( Address node : manager.getMembers() )
				printResult(des.submit(node, new RegularCallable("Task[" + nbTaskToRun-- + "] ran on node with address: " + node.toString())));
	}

	private static void printResult(Future<String> future) {
		try {
			System.out.println("Result:" + future.get());
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		} catch (ExecutionException e) {
			throw new IllegalStateException(e);
		}
	}

	private static void printResults(Collection<Future<String>> futures) {
		for ( Future<String> result : futures)
			printResult(result);
	}
}
