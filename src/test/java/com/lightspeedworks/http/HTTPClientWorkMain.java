package com.lightspeedworks.http;

// http://k-hiura.cocolog-nifty.com/blog/2012/04/javahttphttps-1.html

// http://x68000.q-e-d.net/~68user/net/java-http-url-connection-1.html
// http://x68000.q-e-d.net/~68user/net/java-http-url-connection-2.html
// http://x68000.q-e-d.net/~68user/net/java-http-socket-1.html

public class HTTPClientWorkMain {

	/**
	 * main {メイン}
	 *
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		final String LINE_SEPARATOR = HTTP.LINE_SEPARATOR;
		String resText;
		String url = "http://localhost:1337/";
		// String url = "http://localhost/";

		HTTPClient client = HTTP.createClient();

		resText = client.headRequest(url);
		System.out.println("### main headRequest resText: " + resText);

		resText = client.getRequest(url);
		System.out.println("### main getRequest resText: " + resText);

		String data = "[\"STR漢字\",123,1.23,true,[],{\"x\":1}]" + LINE_SEPARATOR;
		String dataType = "text/plain; charset=utf-8";

		resText = client.postRequest(url, data, dataType);
		System.out.println("### main postRequest resText: " + resText);

		resText = client.putRequest(url, data, dataType);
		System.out.println("### main putRequest resText: " + resText);

		resText = client.deleteRequest(url);
		System.out.println("### main deleteRequest resText: " + resText);

		data = "[\"STR漢字𠀋\"]" + LINE_SEPARATOR;

		resText = client.postRequest(url, data, dataType);
		System.out.println("### main postRequest resText: " + resText);
	}
}
