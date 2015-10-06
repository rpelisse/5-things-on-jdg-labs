package org.jboss.as.labs.datagrid.lab5;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClusterLauncher {

	public static void main(String[] args) {
		ExecutorService executor = Executors.newFixedThreadPool(3); // Sum up wait times to know when to shutdown 
		for (int i=0; i<10; i++) { 			
			executor.execute(new SimpleExecutionNode()); 
			executor.execute(new SimpleExecutionNode()); 
			executor.execute(new DispatcherNode()); 
	   } 
		Scanner scanner = new Scanner(System.in);
		System.out.println("Type anything to stop progrom...");
		String input = scanner.next();
		if ( input != null ) {
			scanner.close();
			try {
			executor.shutdownNow();
			} catch ( Exception e ) {
				System.exit(1);
			}
			System.exit(0);
		}
		 	
	}

}
