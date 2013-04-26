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
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;

/**
 * HTTPClientSocket class.
 *
 * @author nishizawa
 */
public class HTTPClientSocket implements HTTPClient {
	/**
	 * default HTTP port number.
	 */
	static final int DEFAULT_HTTP_PORT = 80;

	/**
	 * hexa-decimal.
	 */
	static final int HEXA_DECIMAL = 16;

	/**
	 * from 2 bytes.
	 */
	static final int U_0080 = 0x0080;
	/**
	 * from 3 bytes.
	 */
	static final int U_0800 = 0x0800;
	/**
	 * begin of surrogate pair.
	 */
	static final int U_D800 = 0xD800;
	/**
	 * end of surrogate pair.
	 */
	static final int U_E000 = 0xE000;
	/**
	 * from 4 bytes.
	 */
	static final int U_10000 = 0x10000;

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
	 * do request by socket.
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

		Socket socket = null;

		PrintWriter pw = null;
		BufferedWriter bw = null;
		BufferedReader br = null;

		try {
			URL urlObj = new URL(url);
			String host = urlObj.getHost();
			String path = urlObj.getPath();
			int port = urlObj.getPort();
			if (port < 0) {
				port = DEFAULT_HTTP_PORT;
			}

			socket = new Socket(host, port);

			bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
			pw = new PrintWriter(bw);

			pw.println(method + " " + path + " HTTP/1.1");
			if (port == DEFAULT_HTTP_PORT) {
				pw.println("Host: " + host);
			} else {
				pw.println("Host: " + host + ":" + port);
			}
			if (data == null) {
				pw.println();
			} else {
				pw.println("Content-Type: " + dataType);
				pw.println("Content-Length: " + data.getBytes("UTF-8").length);
				pw.println();
				pw.print(data);
			}
			pw.flush();

			br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

			String line;
			line = br.readLine();
			if (line == null)
				throw new Exception("unexpected EOF in status");
			System.out.println(method + " code...: " + line);

			HashMap<String, String> map = new HashMap<String, String>();

			// header
			while (true) {
				line = br.readLine();
				if (line == null)
					throw new Exception("unexpected EOF in header");
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
						throw new Exception("unexpected EOF in chunked body 1");
					// System.out.println("@@@ chunk len: " + line);
					int len = Integer.parseInt(line, HEXA_DECIMAL);
					if (len == 0) {
						line = br.readLine();
						if (line == null)
							throw new Exception("unexpected EOF in chunked body 2");
						if (!line.equals(""))
							throw new Exception("new line expected. eh? 1");
						break; // end of chunk
					}
					sb.append(readChunk(br, len));
					line = br.readLine();
					if (line == null)
						throw new Exception("unexpected EOF in chunked body 3");
					if (!line.equals(""))
						throw new Exception("new line expected. eh? 2");
				}
				return sb.toString();
			}

			String clen = map.get("Content-Length");
			if (clen != null) {
				int len = Integer.parseInt(clen); // decimal
				return readChunk(br, len);
			}

			throw new Exception("Content-Length or Transfer-Encoding chunked expected");
		} catch (Exception e) {
			// catch (MalformedURLException e)
			// catch (UnknownHostException e)
			// catch (UnsupportedEncodingException e)
			// catch (IOException e)
			throw e;
		} finally {
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
		}
	}

	/**
	 * read chunk.
	 *
	 * @param br
	 *            BufferedReader
	 * @param len
	 *            int
	 * @return String
	 * @throws Exception
	 *             Network I/O or more Exception
	 */
	static String readChunk(BufferedReader br, int len) throws Exception {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < len; ++i) {
			int ch = br.read();
			if (ch < 0)
				throw new Exception("unexpected EOF in body");

			if (ch >= U_0080 && ch < U_0800) // 2 bytes
				++i;
			else if (ch >= U_D800 && ch < U_E000) // 2 + 2 bytes
				++i;
			else if (ch >= U_0800 && ch < U_10000) // 3 bytes
				i += 2;
			else if (ch >= U_10000) // 4 bytes
				i += 3;
			sb.append((char) ch);
		}
		return sb.toString();
	}
}
