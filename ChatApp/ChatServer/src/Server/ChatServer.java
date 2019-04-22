package Server;

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;

import library.*;

import java.net.ServerSocket;

/*
 * A chat server that delivers public and private messages.
 */
public class ChatServer {

	// The server socket.
	private static ServerSocket serverSocket = null;
	// The client socket.
	private static Socket clientSocket = null;

	// This chat server can accept up to maxClientsCount clients' connections.
	private static final int maxClientsCount = 10;
	private static final clientThread[] threads = new clientThread[maxClientsCount];

	public static void main(String args[]) {

		// The default port number.
		int portNumber = 2222;
		if (args.length < 1) {
			System.out.println(
					"Usage: java MultiThreadChatServerSync <portNumber>\n" + "Now using port number=" + portNumber);
		} else {
			portNumber = Integer.valueOf(args[0]).intValue();
		}

		/*
		 * Open a server socket on the portNumber (default 2222). Note that we
		 * can not choose a port less than 1023 if we are not privileged users
		 * (root).
		 */
		try {
			serverSocket = new ServerSocket(portNumber);
		} catch (IOException e) {
			System.out.println(e);
		}

		/*
		 * Create a client socket for each connection and pass it to a new
		 * client thread.
		 */
		while (true) {
			try {
				clientSocket = serverSocket.accept();
				int i = 0;
				for (i = 0; i < maxClientsCount; i++) {
					if (threads[i] == null) {
						(threads[i] = new clientThread(clientSocket, threads)).start();
						break;
					}
				}
				if (i == maxClientsCount) {
					PrintStream os = new PrintStream(clientSocket.getOutputStream());
					os.println("Server too busy. Try later.");
					os.close();
					clientSocket.close();
				}
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}
}

/*
 * The chat client thread. This client thread opens the input and the output
 * streams for a particular client, ask the client's name, informs all the
 * clients connected to the server about the fact that a new client has joined
 * the chat room, and as long as it receive data, echos that data back to all
 * other clients. The thread broadcast the incoming messages to all clients and
 * routes the private message to the particular client. When a client leaves the
 * chat room this thread informs also all the clients about that and terminates.
 */
class clientThread extends Thread {

	private String clientName = null;
	private DataInputStream is = null;
	private PrintStream os = null;
	private Socket clientSocket = null;
	private final clientThread[] threads;
	private int maxClientsCount;
	private library users = new library();
	private ChatHistory history = null;

	public clientThread(Socket clientSocket, clientThread[] threads) {
		this.clientSocket = clientSocket;
		this.threads = threads;
		maxClientsCount = threads.length;
	}

	@SuppressWarnings("deprecation")
	public void run() {
		int maxClientsCount = this.maxClientsCount;
		clientThread[] threads = this.threads;
		String line = null;
		String name = null;
		try {
			/*
			 * Create input and output streams for this client.
			 */
			is = new DataInputStream(clientSocket.getInputStream());
			os = new PrintStream(clientSocket.getOutputStream());
			do {
				String username, password, retype = null;
				do {

					os.println("Welcome to the chat application.");
					os.println("Do you wish to register, login, or quit?");
					line = is.readLine();
					if (line.contains("register") || line.contains("Register")) {
						do {
							do {
								os.println("Enter the username you want to use (can't start with @).");
								username = is.readLine().trim();
								if (username.startsWith("@"))
									os.println("ERROR: username can't start with @");
							} while (username.startsWith("@"));
							do {
								os.println("Enter password you will use.");
								password = is.readLine().trim();
								os.println("Retype password.");
								retype = is.readLine().trim();
								if (!retype.equals(password))
									os.println("Error: password and retype doesn't match.");
							} while (!retype.equals(password));
							line = register(username + ":" + password, threads);
							if (line.equalsIgnoreCase("error"))
								os.println("Error: Username taken.");
						} while (!line.equalsIgnoreCase("available"));
					} else if (line.contains("login")) {
						while (name == null) {
							os.println("Enter username.");
							username = is.readLine().trim();
							os.println("Enter password");
							password = is.readLine().trim();
							name = login(username + ":" + password);
							if (name == null)
								os.println("Error: Invalid username/password");
						}
						if (name != null)
							break;
					} else if (line.contains("quit")) {
						break;
					} else
						os.println("Error: Invalid input.");

				} while (!line.contains("login") || !line.contains("quit") || name == null);
				/* Welcome the new the client. */
				if (line.equalsIgnoreCase("quit"))
					break;
				os.println("Welcome " + name + " to our chat room.");
				history = new ChatHistory(name);
				synchronized (this) {
					for (int i = 0; i < maxClientsCount; i++) {
						if (threads[i] != null && threads[i] == this) {
							clientName = "@" + name;
							break;
						}
					}
					for (int i = 0; i < maxClientsCount; i++) {
						if (threads[i] != null && threads[i] != this) {
							threads[i].os.println(name + " entered the chat room !!!");
						}
					}
				}
				/* Start the conversation. */
				while (true) {
					line = is.readLine();
					if (line.startsWith("/quit") || line.startsWith("/logout"))
						break;
					else if (line.startsWith("/online"))
						online(threads);
					else if (line.startsWith("/history")) {
							history.displayHistory();
					}
					/* If the message is private sent it to the given client. */
					else if (line.startsWith("@")) {
						privateMsg(name, line, threads);
					} else {

						// The message is public, broadcast it to all other
						// clients.

						publicMsg(name, line, threads);
					}
				}
				synchronized (this) {
					for (int i = 0; i < maxClientsCount; i++) {
						if (threads[i] != null && threads[i] != this && threads[i].clientName != null) {
							threads[i].os.println(name + " is leaving the chat room !!!");
						}
					}
				}
				os.println("Bye " + name);

				/*
				 * Clean up. Set the current thread variable to null so that a
				 * new client could be accepted by the server.
				 */
				clientName = null;
				history = null;
				name = null;
				username = null;
				password = null;
				retype = null;
				for (int i = 0; i < maxClientsCount; ++i) {
					if (threads[i] == this) {
						threads[i].clientName = null;
					}
				}
			} while (line.contains("logout"));
			synchronized (this) {
				for (int i = 0; i < maxClientsCount; i++) {
					if (threads[i] == this) {
						threads[i] = null;
					}
				}
			}
			/*
			 * Close the output stream, close the input stream, close the
			 * socket.
			 */
			is.close();
			os.close();
			clientSocket.close();
		} catch (IOException e) {
		}
	}

	private String register(String line, clientThread[] threads) {
		String[] words = line.split(":", 2);
		int i = users.checkUser(words[0]);
		if (i == 0) {
			synchronized (this) {
				for (int j = 0; j < maxClientsCount; ++j) {
					if (threads[j] != null)
						threads[j].users.insert(new uNode(words[0], words[1]));
				}
			}
			users.appendFile(words[0], words[1]);
			return "available";
		} else
			return "error";
	}

	private String login(String line) {
		String[] words = line.split(":", 2);
		int i = users.check(words[0], words[1]);
		return (i == 0) ? null : words[0];
	}

	private void online(clientThread[] threads) {
		String names = "Online:";
		synchronized (this) {
			for (int i = 0; i < maxClientsCount; ++i) {
				if (threads[i] != null && threads[i] != this && threads[i].clientName != null
						|| !this.clientName.equals(threads[i].clientName)) {
					names = names + threads[i].clientName;
				}
			}
		}
		os.println(names);
	}

	private void privateMsg(String name, String line, clientThread[] threads) {
		String[] words = line.split("\\s", 2);
		if (words.length > 1 && words[1] != null) {
			words[1] = words[1].trim();
			if (!words[1].isEmpty()) {
				synchronized (this) {
					for (int i = 0; i < maxClientsCount; i++) {
						if (threads[i] != null && threads[i] != this && threads[i].clientName != null
								&& threads[i].clientName.equals(words[0])) {
							threads[i].os.println(name + ": " + words[1]);
							threads[i].history.insertMsg(name + ": " + words);
							/*
							 * Echo this message to let the client know the
							 * private message was sent.
							 */
							this.os.println(name + ": " + words[1]);
							this.history.insertMsg(name + ": " + words[1]);
							break;
						}
					}
				}
			}
		}
	}

	private void publicMsg(String name, String line, clientThread[] threads) {
		synchronized (this) {
			for (int i = 0; i < maxClientsCount; i++) {
				if (threads[i] != null && threads[i].clientName != null) {
					threads[i].os.println(name + ": " + line);
					threads[i].history.insertMsg(name + ": " + line);
				}
			}
		}
	}
}
