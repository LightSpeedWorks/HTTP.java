package com.lightspeedworks.http;

/**
 * HTTP class.
 *
 * @see http://k-hiura.cocolog-nifty.com/blog/2012/04/javahttphttps-1.html
 *
 * @see http://x68000.q-e-d.net/~68user/net/java-http-url-connection-1.html
 * @see http://x68000.q-e-d.net/~68user/net/java-http-url-connection-2.html
 * @see http://x68000.q-e-d.net/~68user/net/java-http-socket-1.html
 *
 * @see http://nodejs.org/api/http.html
 * @see http://nodejs.jp/nodejs.org_ja/api/http.html
 *
 * @see http://yand.info/?p=/docs/http.html
 * @see http://jp.yand.info/?p=/docs/http.html
 *
 * @author LightSpeedC (Kazuaki Nishizawa; 西澤 和晃)
 */
class HTTP {
	/**
	 * line separator. (CR/LF) {行区切り(復帰/改行)}
	 */
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	// .lineSeparator(); // JDK 1.7

	/**
	 * use socket. {ソケットを使う}
	 */
	static boolean useSocket = false;

	/**
	 * create client.
	 *
	 * @return HTTPClient
	 */
	public static HTTPClient createClient() {
		return useSocket ? createClientSocket() : createClientConnection();
	}

	/**
	 * create client. (connection)
	 *
	 * @return HTTPClient
	 */
	public static HTTPClient createClientConnection() {
		return new HTTPClientConnection();
	}

	/**
	 * create client. (by socket)
	 *
	 * @return HTTPClient
	 */
	public static HTTPClient createClientSocket() {
		return new HTTPClientSocket();
	}
}
