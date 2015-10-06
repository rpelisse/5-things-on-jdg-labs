package org.jboss.as.labs.datagrid.lab5;

import java.io.Serializable;
import java.util.Set;

import org.infinispan.Cache;
import org.infinispan.distexec.DistributedCallable;

public class InfinispanDistributableCallable implements
		DistributedCallable<Integer, String, String>,Serializable {

	private static final long serialVersionUID = -7482292875271556249L;
	
	private Cache<Integer, String> cache;
	private Set<Integer> keys;
	
	@Override
	public String call() throws Exception {
		String result = "";
		for ( Object key : keys)
			result += "[key" + key + ",value=" + cache.get(key) + "]";
		System.out.println("This node has computed:" + result);
		return result;
	}

	@Override
	public void setEnvironment(Cache<Integer, String> cache, Set<Integer> keys) {
		this.cache = cache;
		this.keys = keys;
	}


}
