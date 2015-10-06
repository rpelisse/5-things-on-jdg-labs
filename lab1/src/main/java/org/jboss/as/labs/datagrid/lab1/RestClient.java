package org.jboss.as.labs.datagrid.lab1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class RestClient {

	private static final String CACHE_NAME = "namedCache";
	private static final String SERVER_URL = "http://localhost:8080/rest/"
			+ CACHE_NAME;
	private static final String CONTENT_TYPE_FIELD = "Content-Type";
	private static final String CONTENT_TYPE = "text/plain";
	private static final String HTTP_GET_OP_NAME = "GET";
	private static final String HTTP_PUT_OP_NAME = "PUT";

	public static void put(String key, String value) throws IOException {

		final HttpURLConnection connection = buildConnection(HTTP_PUT_OP_NAME, key);

		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
				connection.getOutputStream());
		outputStreamWriter.write(value);

		outputStreamWriter.flush();
		printResponseMessage(HTTP_PUT_OP_NAME, connection);
		connection.disconnect();

	}


	private static HttpURLConnection buildConnection(String httpOpName, String key) throws IOException {
		final URL address = new URL(SERVER_URL + "/" + key);

		HttpURLConnection connection = (HttpURLConnection) address
				.openConnection();
		connection.setRequestMethod(httpOpName);
		connection.setRequestProperty(CONTENT_TYPE_FIELD,CONTENT_TYPE);
		connection.setDoOutput(true);
		return connection;
	}

	public static String get(String key) throws IOException {

		HttpURLConnection connection = buildConnection(HTTP_GET_OP_NAME, key);

		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(connection.getInputStream()));

		String line = new String();
		StringBuilder stringBuilder = new StringBuilder();
		while ((line = bufferedReader.readLine()) != null) {
			stringBuilder.append(line + '\n');
		}
		printResponseMessage(HTTP_GET_OP_NAME, connection);
		connection.disconnect();
		return stringBuilder.toString();
	}

	private static void printResponseMessage(String httpOpName, HttpURLConnection connection) throws IOException {
		System.out.println(httpOpName + "[" + connection.getResponseMessage() + ":" + connection.getResponseCode() + "]");
	}

	public static void main(String[] args) throws IOException {
		if ( args.length != 2 )
			throw new IllegalArgumentException("At least arguments are required: <key> <value>.");

		final String key = args[0];
		final String value = args[1];

		put(key, value);
		System.out.println("Value for '" + key.trim() + "' is '" + get(key).trim() + "'");
	}
}
