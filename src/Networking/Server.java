package Networking;

import sun.misc.IOUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {

	private int port;
	private List<OutputStream> clients;
	private ServerSocket server;

	public static void main(String[] args) throws IOException {
		new Server(12345).run();
	}

	public Server(int port) {
		this.port = port;
		this.clients = new ArrayList<>();
	}

	public void run() {
		try {
			server = new ServerSocket(port) {
				protected void finalize() throws IOException {
					this.close();
				}
			};
			System.out.println("Port 12345 is now open.");

			while (true) {
				// accepts a new client
				Socket client = server.accept();
				System.out.println("Connection established with client: " + client.getInetAddress().getHostAddress());


				// add client message to list
				this.clients.add(client.getOutputStream());

				// create a new thread for client handling
				new Thread(new ClientHandler(this, client.getInputStream())).start();
			}
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
		}
	}

	void broadcastMessages(byte[] data) {
		for (OutputStream client : this.clients) {
		    try {
		        DataOutputStream os = new DataOutputStream(client);
		        os.writeInt(data.length);
                os.write(data);
            } catch (IOException e) {
                System.out.println(e.getLocalizedMessage());
            }
		}
	}
}

class ClientHandler implements Runnable {

	private Server server;
	private InputStream client;

	public ClientHandler(Server server, InputStream client) {
		this.server = server;
		this.client = client;
	}

	@Override
	public void run() {
		byte[] message;
		DataInputStream is = new DataInputStream(client);

		try {
            // when there is a new message, broadcast to all
            int length;
            while ((length = is.readInt()) > 0) {
                message = new byte[length];
                is.readFully(message, 0, message.length);
                server.broadcastMessages(message);
            }
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
	}
}
