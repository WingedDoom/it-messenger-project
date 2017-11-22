package Networking;

import DSA.DataManager;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

	private String host;
	private int port;
	private String nickname;
	private DataManager dataManager;

	public static void main(String[] args) throws IOException {
		new Client("127.0.0.1", 12345).run();
	}

	public Client(String host, int port) {
		this.host = host;
		this.port = port;
		this.dataManager = new DataManager();
	}

	public void run() throws IOException {
		// connect client to server
		Socket client = new Socket(host, port);
		System.out.println("Client successfully connected to server!");

		// create a new thread for server messages handling
		new Thread(new ReceivedMessagesHandler(client.getInputStream(), this)).start();

		Scanner sc = new Scanner(System.in);

		// read messages from keyboard and send to server
		System.out.println("Send messages: ");
		DataOutputStream output = new DataOutputStream(client.getOutputStream());
		while (sc.hasNextLine()) {
		    String line = sc.nextLine();
		    String[] parts = line.split(" ");

            byte[] bytes;

		    if (parts.length > 1) {
		        if (parts[0].equals("file")) {
		            bytes = dataManager.compressAndEncodeFileAtPath(parts[1]);
                    System.out.println("Sending file at path: " + parts[1]);
                } else {
		            bytes = dataManager.compressAndEncodeMessage(line);
                }
            } else {
                bytes = dataManager.compressAndEncodeMessage(line);
            }

			output.writeInt(bytes.length);
			output.write(bytes);
		}
		
		output.close();
		sc.close();
		client.close();
	}

    public DataManager getDataManager() {
        return dataManager;
    }
}

class ReceivedMessagesHandler implements Runnable {

	private InputStream server;
	private Client client;

	public ReceivedMessagesHandler(InputStream server, Client client) {
		this.server = server;
		this.client = client;
	}

	@Override
	public void run() {
		// receive server messages and print out to screen
        int length;
        DataInputStream is = new DataInputStream(server);

        try {
            while ((length = is.readInt()) > 0) {
                byte[] data = new byte[length];

                is.readFully(data, 0, data.length);
                String message = client.getDataManager().getMessage(data);

                System.out.println("New message: " + message);
            }
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
	}
}
