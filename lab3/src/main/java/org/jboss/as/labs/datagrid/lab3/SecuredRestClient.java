package org.jboss.as.labs.datagrid.lab3;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class SecuredRestClient {

	private static final String CACHE_NAME = "namedCache";
	private static final String SERVER_DOMAIN_NAME = "localhost";
	private static final String SERVER_URL = "http://" + SERVER_DOMAIN_NAME + ":8080/rest/"
			+ CACHE_NAME;
	
	private static final String USERNAME = "demo";
	private static final String PASSWORD = "demo";
	
	private static CredentialsProvider setAuth() {
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(SERVER_DOMAIN_NAME, 8080),
				new UsernamePasswordCredentials(USERNAME, PASSWORD));	
		return credsProvider;
	}
	
	public static String get(String key) throws IOException {
		CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(setAuth()).build();
		try {
			HttpGet httpget = new HttpGet(SERVER_URL + "/" + key);
			httpget.setHeader("Content-Type", "text/plain");
			System.out.println("Executing request " + httpget.getRequestLine());
			ResponseHandler<String> responseHandler = null; // FIXME: Implements an handler that handles errors
			String responseBody = httpclient.execute(httpget, responseHandler);
			return responseBody.toString();
		} finally {
			httpclient.close();
		}
	}

	private static HttpEntity buildHttpEntity(String value) throws IOException {
		InputStreamEntity reqEntity = new InputStreamEntity(
				new ByteArrayInputStream(
						value.getBytes(StandardCharsets.UTF_8)), -1,
				ContentType.APPLICATION_OCTET_STREAM);
		reqEntity.setChunked(true);

		return new BufferedHttpEntity(reqEntity);
		
	}
	
	public static void put(String key, String value) throws IOException {
		CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(setAuth()).build();
		try {
			HttpPut httpput = new HttpPut(SERVER_URL + "/" + key);
			httpput.setHeader("Content-Type", "text/plain");			
			httpput.setEntity(buildHttpEntity(value));

			System.out.println("Executing request " + httpput.getRequestLine());

			ResponseHandler<String> responseHandler = null; // FIXME: Implements
															// an handler - note 
															// that it should 
															// handle 404 response
															// by returning an
															// empty string
			String responseBody = httpclient.execute(httpput, responseHandler);
			System.out.println(responseBody);
		} finally {
			httpclient.close();
		}
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 2)
			throw new IllegalArgumentException(
					"At least arguments are required: <key> <value>.");

		final String key = args[0];
		final String value = args[1];

		put(key, value);
		System.out.println(get(key));
	}
}
