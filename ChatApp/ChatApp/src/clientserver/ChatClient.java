package clientserver;

import java.io.*;
import java.net.*;

@SuppressWarnings("deprecation")
public class ChatClient {
	private static Socket clientSocket = null;
	private static DataInputStream is = null;
	private static PrintStream os = null;
	private static ChatWindow window = new ChatWindow(null);

	public static void main(String[] args) {

		/*
		 * Open a socket on port 2222. Open the input and the output streams.
		 */
		try {
			clientSocket = new Socket("localhost", 2222);
			os = new PrintStream(clientSocket.getOutputStream());
			is = new DataInputStream(clientSocket.getInputStream());
			new DataInputStream(new BufferedInputStream(System.in));
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host");
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to host");
		}

		/*
		 * If everything has been initialized then we want to write some data to
		 * the socket we have opened a connection to on port 2222.
		 */
		if (clientSocket != null && os != null && is != null) {
			try {
				/*
				 * Keep on reading from/to the socket till we receive the "Ok"
				 * from the server, once we received that then we break.
				 */
				String responseLine;
				while ((responseLine = is.readLine()) != null) {
					if (responseLine.equalsIgnoreCase("/quit") || responseLine.equalsIgnoreCase("/logout")) {
						break;
					} else {
						if (responseLine.startsWith("Online:")) {
							String[] lines = responseLine.trim().split(":", 2);
							if (lines[1] == null || lines[1].trim().isEmpty())
								window.writeToChat("No other users online");
							else
								window.openOnline(lines[1]);
						} else if (responseLine.contains("Bye") || responseLine.contains("Welcome")) {
							window.cleanChat();
							window.writeToChat(responseLine);
						} else
							window.writeToChat(responseLine);
					}
				}

				/*
				 * Close the output stream, close the input stream, close the
				 * socket.
				 */
				window.close();
				os.close();
				is.close();
				clientSocket.close();
			} catch (UnknownHostException e) {
				System.err.println("Trying to connect to unknown host: " + e);
			} catch (IOException e) {
				System.err.println("IOException:  " + e);
			}
		}
	}

	public static void out(String text) {
		os.println(text);
	}
}