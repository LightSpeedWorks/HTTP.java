package com.lightspeedworks.http;

// http://k-hiura.cocolog-nifty.com/blog/2012/04/javahttphttps-1.html

// http://x68000.q-e-d.net/~68user/net/java-http-url-connection-1.html
// http://x68000.q-e-d.net/~68user/net/java-http-url-connection-2.html
// http://x68000.q-e-d.net/~68user/net/java-http-socket-1.html

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author nishizawa
 *
 */
public class HTTPClientConnection implements HTTPClient {
	/**
	 * HEAD request.
	 *
	 * @param url
	 *            String URL
	 * @return String response text
	 * @throws Exception
	 *             Network I/O or more Exception
	 */
	public String headRequest(String url) throws Exception {
		return doRequest("HEAD", url, null, null);
	}

	/**
	 * GET request.
	 *
	 * @param url
	 *            String URL
	 * @return String response text
	 * @throws Exception
	 *             Network I/O or more Exception
	 */
	public String getRequest(String url) throws Exception {
		return doRequest("GET", url, null, null);
	}

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
	public String postRequest(String url, String data, String dataType) throws Exception {
		return doRequest("POST", url, data, dataType);
	}

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
	public String putRequest(String url, String data, String dataType) throws Exception {
		return doRequest("PUT", url, data, dataType);
	}

	/**
	 * DELETE request.
	 *
	 * @param url
	 *            String URL
	 * @return String response text
	 * @throws Exception
	 *             Network I/O or more Exception
	 */
	public String deleteRequest(String url) throws Exception {
		return doRequest("DELETE", url, null, null);
	}

	// *OPTIONS*
	// public String optionsRequest(String url) {
	// return doRequest("OPTIONS", url, null, null);
	// }

	// *TRACE*
	// public String traceRequest(String url) {
	// return doRequest("TRACE", url, null, null);
	// }

	/**
	 * do request by HttpURLConnection.
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
	public String doRequest(String method, String url, String data, String dataType) throws Exception {
		System.out.print(HTTP.LINE_SEPARATOR + "########################################");

		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setInstanceFollowRedirects(false);
		conn.setRequestProperty("Accept-Language", "ja;q=1.0,en;q=0.1");
		conn.setRequestMethod(method);
		if (data != null) {
			conn.setDoOutput(true); // POST用データが存在する
			conn.setRequestProperty("Content-Type", dataType);
		}
		conn.connect(); // 接続 & 送信

		if (data != null) {
			PrintWriter pw = new PrintWriter(
					new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8")));
			pw.print(data);
			pw.close();
		}

		// Map<String, List<String>> map = conn.getHeaderFields();
		// for (String key : map.keySet())
		// if (key == null)
		// System.out.println(method + " code...: " + map.get(key).get(0));
		// else
		// System.out.println(method + " head...: " + key + ": " +
		// map.get(key).get(0));

		// body部の文字コード取得
		String charSet = "UTF-8"; // "Shift-JIS" "ISO-8859-1";
		// String resContentType = conn.getHeaderField("Content-Type");
		// if (resContentType != null) {
		// for (String elem : resContentType.replace(" ", "").split(";")) {
		// if (elem.startsWith("charset=")) {
		// charSet = elem.substring(8);
		// break;
		// }
		// }
		// }

		// body部受信
		BufferedReader br;
		br = new BufferedReader(new InputStreamReader(conn.getInputStream(), charSet));
		// try {
		// br = new BufferedReader(new InputStreamReader(
		// conn.getInputStream(), charSet));
		// } catch (Exception e) {
		// e.printStackTrace(System.err);
		// System.out.println(conn.getResponseCode() + " "
		// + conn.getResponseMessage());
		// br = new BufferedReader(new InputStreamReader(
		// conn.getErrorStream(), charSet));
		// }
		StringBuilder sb = new StringBuilder();
		int ch;
		while ((ch = br.read()) >= 0) {
			sb.append((char) ch);
		}
		br.close();
		br = null;
		conn.disconnect();
		return sb.toString();

		// } catch (MalformedURLException e) {
		// e.printStackTrace(System.err);
		// } catch (ProtocolException e) {
		// e.printStackTrace(System.err);
		// } catch (IOException e) {
		// e.printStackTrace(System.err);
		// } catch (Exception e) {
		// throw e;
		// // e.printStackTrace(System.err);
		// }
		// return null;
	}
}
