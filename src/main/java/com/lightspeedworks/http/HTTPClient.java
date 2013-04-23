package com.lightspeedworks.http;

// http://k-hiura.cocolog-nifty.com/blog/2012/04/javahttphttps-1.html

// http://x68000.q-e-d.net/~68user/net/java-http-url-connection-1.html
// http://x68000.q-e-d.net/~68user/net/java-http-socket-1.html

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;

public class HTTPClient {
	/**
	 * line separator (CR/LF) {行区切り(復帰/改行)}
	 */
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	/**
	 * use socket {ソケットを使う}
	 */
	static final boolean useSocket = false;

	/**
	 * HEAD request
	 *
	 * @param url
	 * @return String
	 * @throws Exception
	 */
	public String headRequest(String url) throws Exception {
		return doRequest("HEAD", url, null, null);
	}

	/**
	 * GET request
	 *
	 * @param url
	 * @return String
	 * @throws Exception
	 */
	public String getRequest(String url) throws Exception {
		return doRequest("GET", url, null, null);
	}

	/**
	 * POST request
	 *
	 * @param url
	 * @param data
	 * @param dataType
	 * @return String
	 * @throws Exception
	 */
	public String postRequest(String url, String data, String dataType) throws Exception {
		return doRequest("POST", url, data, dataType);
	}

	/**
	 * PUT request
	 *
	 * @param url
	 * @param data
	 * @param dataType
	 * @return String
	 * @throws Exception
	 */
	public String putRequest(String url, String data, String dataType) throws Exception {
		return doRequest("PUT", url, data, dataType);
	}

	/**
	 * DELETE request
	 *
	 * @param url
	 * @return String
	 * @throws Exception
	 */
	public String deleteRequest(String url) throws Exception {
		return doRequest("DELETE", url, null, null);
	}

	// *OPTIONS*
	// public String optionsRequest(String url) {
	//	return doRequest("OPTIONS", url, null, null);
	// }

	// *TRACE*
	// public String traceRequest(String url) {
	//	return doRequest("TRACE", url, null, null);
	// }

	/**
	 * do request
	 *
	 * @param method
	 * @param url
	 * @param data
	 * @param dataType
	 * @return String
	 * @throws Exception
	 */
	public String doRequest(String method, String url, String data, String dataType) throws Exception {
		System.out.print(LINE_SEPARATOR + "########################################");
		if (useSocket) {
			System.out.println(" Socket");
			return doRequestSocket(method, url, data, dataType);
		}
		else {
			System.out.println(" HttpURLConnection");
			return doRequestHttpURLConnection(method, url, data, dataType);
		}
	}

	/**
	 * do request by HttpURLConnection
	 *
	 * @param method
	 * @param url
	 * @param data
	 * @param dataType
	 * @return String
	 * @throws Exception
	 */
	public String doRequestHttpURLConnection(String method, String url, String data, String dataType) throws Exception {
		try {
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
						new BufferedWriter(new OutputStreamWriter(
								conn.getOutputStream(), "UTF-8")));
				pw.print(data);
				pw.close();
			}

			// Map<String, List<String>> map = conn.getHeaderFields();
			// for (String key : map.keySet())
			//	if (key == null)
			//		System.out.println(method + " code...: " + map.get(key).get(0));
			//	else
			//		System.out.println(method + " head...: " + key + ": " + map.get(key).get(0));

			// body部の文字コード取得
			String charSet = "UTF-8"; // "Shift-JIS" "ISO-8859-1";
			String resContentType = conn.getHeaderField("Content-Type");
			if (resContentType != null) {
				for (String elem : resContentType.replace(" ", "").split(";")) {
					if (elem.startsWith("charset=")) {
						charSet = elem.substring(8);
						break;
					}
				}
			}

			// body部受信
			BufferedReader br;
			try {
				br = new BufferedReader(new InputStreamReader(
						conn.getInputStream(), charSet));
			} catch (Exception e) {
				e.printStackTrace(System.err);
				System.out.println(conn.getResponseCode() + " "
						+ conn.getResponseMessage());
				br = new BufferedReader(new InputStreamReader(
						conn.getErrorStream(), charSet));
			}
			StringBuilder sb = new StringBuilder();
			int ch;
			while ((ch = br.read()) >= 0)
				sb.append((char) ch);
			br.close();
			conn.disconnect();
			return sb.toString();
//		} catch (MalformedURLException e) {
//			e.printStackTrace(System.err);
//		} catch (ProtocolException e) {
//			e.printStackTrace(System.err);
//		} catch (IOException e) {
//			e.printStackTrace(System.err);
		} catch (Exception e) {
			throw e;
			// e.printStackTrace(System.err);
		}
		// return null;
	}

	/**
	 * do request by socket
	 *
	 * @param method
	 * @param url
	 * @param data
	 * @param dataType
	 * @return String
	 * @throws Exception
	 */
	public String doRequestSocket(String method, String url, String data, String dataType) throws Exception {
		Socket socket = null;
		PrintWriter pw = null;
		BufferedWriter bw = null;
		BufferedReader br = null;
		try {
			URL urlObj = new URL(url);
			String host = urlObj.getHost();
			String path = urlObj.getPath();
			int port = urlObj.getPort();
			if (port < 0)
				port = 80;

			socket = new Socket(host, port);
			bw = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream(), "UTF-8"));
			pw = new PrintWriter(bw);

			pw.println(method + " " + path + " HTTP/1.1");
			if (port == 80)
				pw.println("Host: " + host);
			else
				pw.println("Host: " + host + ":" + port);
			if (data == null)
				pw.println();
			else {
				pw.println("Content-Type: " + dataType);
				pw.println("Content-Length: " + data.getBytes("UTF-8").length);
				pw.println();
				pw.print(data);
			}
			pw.flush();

			br = new BufferedReader(new InputStreamReader(
					socket.getInputStream(), "UTF-8"));

			String line;
			line = br.readLine();
			if (line == null)
				throw new Error("unexpected EOF in status");
			System.out.println(method + " code...: " + line);

			HashMap<String, String> map = new HashMap<String, String>();

			// header
			while (true) {
				line = br.readLine();
				if (line == null)
					throw new Error("unexpected EOF in header");
				if (line.equals(""))
					break;
				int pos = line.indexOf(": ");
				String key = line.substring(0, pos);
				String val = line.substring(pos + 2);
				map.put(key, val);
				// System.out.println(method + " head...: " + line);
			}

			if (method.equals("HEAD"))
				return null;

			StringBuilder sb = new StringBuilder();

			String chunked = map.get("Transfer-Encoding");
			if (chunked != null && chunked.equals("chunked")) {
				while (true) {
					// chunk length
					line = br.readLine();
					if (line == null)
						throw new Error("unexpected EOF in chunked body 1");
					// System.out.println("@@@ chunk len: " + line);
					int len = Integer.parseInt(line, 16); // hexa-decimal
					if (len == 0) {
						line = br.readLine();
						if (line == null)
							throw new Error("unexpected EOF in chunked body 2");
						if (!line.equals(""))
							throw new Error("eh? 1");
						break; // end of chunk
					}
					sb.append(readChunk(br, len));
					line = br.readLine();
					if (line == null)
						throw new Error("unexpected EOF in chunked body 3");
					if (!line.equals(""))
						throw new Error("eh? 2");
				}
				return sb.toString();
			}

			String clen = map.get("Content-Length");
			if (clen != null) {
				int len = Integer.parseInt(clen); // decimal
				return readChunk(br, len);
			}

			throw new Error("Content-Length or Transfer-Encoding chunked expected");
//		} catch (MalformedURLException e) {
//			e.printStackTrace(System.err);
//		} catch (UnknownHostException e) {
//			e.printStackTrace(System.err);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace(System.err);
//		} catch (IOException e) {
//			e.printStackTrace(System.err);
		} catch (Exception e) {
			throw e;
			//e.printStackTrace(System.err);
		} finally {
			try {
				//System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ finally");
				if (br != null) {
					br.close();
					br = null;
				}
				if (pw != null) {
					pw.close();
					pw = null;
				}
				if (bw != null) {
					bw.close();
					bw = null;
				}
				if (socket != null) {
					socket.close();
					socket = null;
				}
			} catch (IOException e) {
				e.printStackTrace(System.err);
			}

		}
		// return null;
	}

	/**
	 * read chunk
	 *
	 * @param br
	 * @param len
	 * @return String
	 * @throws IOException
	 * @throws Exception
	 */
	static String readChunk(BufferedReader br, int len) throws IOException, Exception {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < len; ++i) {
			int ch = br.read();
			if (ch < 0)
				throw new Exception("unexpected EOF in body");

			if (ch >= 0x80 && ch < 0x800) // 2 bytes
				++i;
			else if (ch >= 0xD800 && ch < 0xE000) // 2 + 2 bytes (Surrogate pair)
				++i;
			else if (ch >= 0x800 && ch < 0x10000) // 3 bytes
				i += 2;
			else if (ch >= 0x10000) // 4 bytes
				i += 3;
			sb.append((char) ch);
			//	if (ch < 0xDC00 || ch >= 0xE000)
			//		System.out.print(String.format("&&& %06x [%d]: $$$ ", ch, i));
			//	if (ch >= 0x20)
			//		System.out.print((char) ch);
			//	if (ch >= 0xDC00 && ch < 0xE000)
			//		System.out.print(String.format(" &&& %06x &&&", ch));
			//	if (ch < 0xD800 || ch >= 0xDC00)
			//		System.out.println();
		}
		return sb.toString();
	}
}
