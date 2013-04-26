package com.lightspeedworks.http;

// http://k-hiura.cocolog-nifty.com/blog/2012/04/javahttphttps-1.html

// http://x68000.q-e-d.net/~68user/net/java-http-url-connection-1.html
// http://x68000.q-e-d.net/~68user/net/java-http-socket-1.html

//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.io.PrintWriter;
//import java.net.HttpURLConnection;
//import java.net.Socket;
//import java.net.URL;
//import java.util.HashMap;

/**
 * HTTPClient interface.
 *
 * @author nishizawa
 */
public interface HTTPClient {
	/**
	 * HEAD request.
	 *
	 * @param url
	 *            String URL
	 * @return String response text
	 * @throws Exception
	 *             Network I/O or more Exception
	 */
	String headRequest(String url) throws Exception;

	/**
	 * GET request.
	 *
	 * @param url
	 *            String URL
	 * @return String response text
	 * @throws Exception
	 *             Network I/O or more Exception
	 */
	String getRequest(String url) throws Exception;

	/**
	 * POST request.
	 *
	 * @param url
	 *            String URL
	 * @param data
	 *            String
	 * @param dataType
	 *            String
	 * @return String
	 * @throws Exception
	 *             Network I/O or more Exception
	 */
	String postRequest(String url, String data, String dataType) throws Exception;

	/**
	 * PUT request.
	 *
	 * @param url
	 *            String URL
	 * @param data
	 *            String
	 * @param dataType
	 *            String
	 * @return String
	 * @throws Exception
	 *             Network I/O or more Exception
	 */
	String putRequest(String url, String data, String dataType) throws Exception;

	/**
	 * DELETE request.
	 *
	 * @param url
	 *            String URL
	 * @return String response text
	 * @throws Exception
	 *             Network I/O or more Exception
	 */
	String deleteRequest(String url) throws Exception;

	// *OPTIONS*
	// String optionsRequest(String url);

	// *TRACE*
	// public String traceRequest(String url);

	/**
	 * do request.
	 *
	 * @param method
	 *            String
	 * @param url
	 *            String URL
	 * @param data
	 *            String
	 * @param dataType
	 *            String
	 * @return String
	 * @throws Exception
	 *             Network I/O or more Exception
	 */
	String doRequest(String method, String url, String data, String dataType) throws Exception;
}
