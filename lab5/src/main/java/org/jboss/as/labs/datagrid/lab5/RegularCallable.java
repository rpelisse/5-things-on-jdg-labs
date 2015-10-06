package org.jboss.as.labs.datagrid.lab5;

import java.io.Serializable;
import java.util.concurrent.Callable;

public class RegularCallable implements Callable<String>, Serializable {

	private static int nbCall = 0;
	
	private static final long serialVersionUID = 1L;

	private final String payload;
	
	public RegularCallable(String payload) {
		this.payload = payload;
	}
	
	@Override
	public String call() throws Exception {
		System.out.println("Payload is:" + payload);
		nbCall++;
		System.out.println("Called " + nbCall + "times.");
		return this.payload;
	}
}
