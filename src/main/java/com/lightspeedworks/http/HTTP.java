package com.lightspeedworks.http;

//
// http://k-hiura.cocolog-nifty.com/blog/2012/04/javahttphttps-1.html

// http://x68000.q-e-d.net/~68user/net/java-http-url-connection-1.html
// http://x68000.q-e-d.net/~68user/net/java-http-url-connection-2.html
// http://x68000.q-e-d.net/~68user/net/java-http-socket-1.html

// http://nodejs.org/api/http.html
// http://nodejs.jp/nodejs.org_ja/api/http.html
//
// http://yand.info/?p=/docs/http.html
// http://jp.yand.info/?p=/docs/http.html

/**
 * HTTP class.
 *
 * @author nishizawa
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
	 *
	 * @return HTTPClient
	 */
	public static HTTPClient createClient() {
		return useSocket ? createClientSocket() : createClientConnection();
	}

	/**
	 *
	 * @return HTTPClient
	 */
	public static HTTPClient createClientConnection() {
		return new HTTPClientConnection();
	}

	/**
	 *
	 * @return HTTPClient
	 */
	public static HTTPClient createClientSocket() {
		return new HTTPClientSocket();
	}
}
