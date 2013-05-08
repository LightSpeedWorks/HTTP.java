package com.lightspeedworks.http;

// SocketServer

// http://www.syboos.jp/java/doc/web-http-server-by-socket.html

// シンプルなHTTPサーバを書いた
// http://sinsengumi.net/blog/2012/03/%E3%82%B7%E3%83%B3%E3%83%97%E3%83%AB%E3%81%AA-http-server-%E6%9B%B8%E3%81%84%E3%81%9F/

// httpserver

// http://docs.oracle.com/javase/jp/6/jre/api/net/httpserver/spec/com/sun/net/httpserver/package-summary.html
// http://d.hatena.ne.jp/maji-KY/20110923/1316779604
// https://gist.github.com/maji-KY/1237148

// http://pieceofnostalgy.blogspot.jp/2012/01/java-httpserverimgpost.html

// 記事
// http://codezine.jp/article/corner/33

// google: java http server

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Minimum HTTP Server.
 *
 * @author syboos.jp
 */
public class HTTPServerSocket extends Thread {
	/**
	 * default HTTP port number.
	 */
	static final int DEFAULT_HTTP_PORT = 8080;

	/**
	 * hexa-decimal.
	 */
	static final int HEXA_DECIMAL = 16;

	/**
	 * continue service.
	 */
	static boolean continueService = true;

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
	 * main.
	 *
	 * @param args
	 *            String... arguments
	 */
	public static void main(String... args) {
		listen(DEFAULT_HTTP_PORT);
	}

	/**
	 * HTTP Server listen.
	 *
	 * @param port
	 *            port number (int)
	 */
	public static void listen(int port) {
		ServerSocket serverSocket = null;
		int connectionCount = 1;
		try {
			// サーバサイドのSocketインスタンスを生成
			serverSocket = new ServerSocket(port);
			System.out.println("Web Server is listening on port " + serverSocket.getLocalPort());
			while (continueService) {
				// 接続待ち
				Socket clientSocket = serverSocket.accept();
				// 接続処理スレッド
				HTTPServerSocket thread = new HTTPServerSocket(clientSocket, connectionCount);
				thread.start();
				connectionCount++;
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * client socket.
	 */
	Socket clientSocket;

	/**
	 * conncetion number.
	 */
	int connectionNo;

	/**
	 * constractor.
	 *
	 * @param aClientSocket
	 *            Socket
	 * @param aConnectionNo
	 *            int
	 */
	public HTTPServerSocket(Socket aClientSocket, int aConnectionNo) {
		clientSocket = aClientSocket;
		connectionNo = aConnectionNo;
	}

	// 接続処理スレッド

	/**
	 * run.
	 */
	public void run() {
		try {
			// クライアント IP
			String destIP = clientSocket.getInetAddress().toString();
			// クライアント ポート
			int destPort = clientSocket.getPort();
			System.out.println("Conn#" + connectionNo + ": ################ connected from " + destIP + " with port "
					+ destPort + ".");

			PrintStream outstream = new PrintStream(clientSocket.getOutputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			while (continueService) {
				System.out.println("Conn#" + connectionNo + ": @@@@@@@@@@@@@@@@ wait for request");

				/**
				 * request.
				 */
				String line = br.readLine();
				if (line == null)
					break;
				System.out.print("Conn#" + connectionNo + ": %%");
				String[] request = line.split(" ");
				if (request.length < 3) {
					br.close();
					break;
				}
				String method = request[0];
				String path = request[1];
				String version = request[2];
				System.out.print(" method='" + method + "',");
				System.out.print(" path='" + path + "',");
				System.out.print(" version='" + version + "'");
				for (String elem : request)
					System.out.print(" [" + elem + "]");
				System.out.println();

				if (path.equals("/stop")) // stop the world! STOP SERVICE!
					continueService = false;

				Map<String, List<String>> headersMap = new LinkedHashMap<String, List<String>>();
				List<String> list = new ArrayList<String>();
				list.add(line);
				headersMap.put(null, list);

				/**
				 * headers.
				 */
				while ((line = br.readLine()) != null) {
					if (line.equals("")) {
						/**
						 * end of headers.
						 */
						System.out.println("Conn#" + connectionNo + ": -- request finish!");
						break;
					}
					System.out.println("Conn#" + connectionNo + ": ** " + line);

					int pos = line.indexOf(": ");
					String key, val;
					if (pos >= 0) {
						key = line.substring(0, pos);
						val = line.substring(pos + 2);
					} else {
						key = null;
						val = line;
					}
					list = headersMap.get(key);
					if (list == null) {
						list = new ArrayList<String>();
						headersMap.put(key, list);
					}
					list.add(val);
				}

				for (String key : headersMap.keySet()) {
					System.out.print("Conn#" + connectionNo + ": == " + key + ":");
					for (String val : headersMap.get(key))
						System.out.print(" {" + val + "}");
					System.out.println();
				}

				if (method.equals("POST") || method.equals("PUT")) {
					/**
					 * body.
					 */
					String chunked = null;
					list = headersMap.get("Transfer-Encoding");
					if (list != null)
						chunked = list.get(0);

					int len = 0;
					list = headersMap.get("Content-Length");
					if (list != null)
						len = Integer.parseInt(list.get(0));

					String data;
					if (chunked != null && chunked.equals("chunked")) {
						StringBuilder sb = new StringBuilder();
						while (true) {
							// chunk length
							line = br.readLine();
							if (line == null)
								throw new Error("unexpected EOF in chunked body 1");
							System.out.println("Conn#" + connectionNo + ": !! chunk len: " + line);
							len = Integer.parseInt(line, HEXA_DECIMAL);
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
						data = sb.toString();
					} else {
						data = readChunk(br, len);
					}

					System.out.println("Conn#" + connectionNo + ": ^^ data: " + data);
				}

				/**
				 * response.
				 */
				outstream.println("HTTP/1.1 200 OK");
				outstream.println("Connection: keep-alive");
				outstream.println("Content-Type: text/plain; charset=UTF-8");
				outstream.println("Content-Length: 16");
				outstream.println();
				outstream.println("[\"Hello World.\"]");
				outstream.flush();
			}
			outstream.close();
		} catch (Exception e) {
			System.out.println("Conn#" + connectionNo + ": $$$$$$$$$$$$$$$$ " + e.toString());
			e.printStackTrace(System.out);
			System.out.println("Conn#" + connectionNo + ": $$$$$$$$$$$$$$$$ closing (error)");
			return;
		}
		System.out.println("Conn#" + connectionNo + ": $$$$$$$$$$$$$$$$ closing");
		return;
	}

	/**
	 * read chunk.
	 *
	 * @param br
	 *            BufferedReader
	 * @param len
	 *            length of chunk
	 * @return String chunk
	 * @throws Exception
	 *             Network I/O Exception
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
