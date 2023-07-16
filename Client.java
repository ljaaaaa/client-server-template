import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Client Class
 * A class that represent a client
 * All client side communication is done here
 */
public class Client {
	private static Socket socket;

	private OutputStream outStream;
	private InputStream inStream;

	public BufferedReader br;
	public BufferedWriter bw;

	public String name;

	public final int PORT = 1234;
	public final String HOST = "localhost";

	public static void main(String[] args) {
		new Client();
	}

	public Client(){
		Scanner scanner = new Scanner(System.in);
		System.out.println("Welcome to Client-Server-Template!");

		//Initialize socket and streams
		try {
			socket = new Socket(HOST, PORT);
			outStream = socket.getOutputStream();
			inStream = socket.getInputStream();

		} catch (IOException e) {
			System.out.println("An IOException Occured!");
			System.out.println("Check: Is the server running? Are your HOST and PORT variables correct?");
		}

		//Initialize readers and writers
		br = new BufferedReader(new InputStreamReader(inStream));
		bw = new BufferedWriter(new OutputStreamWriter(outStream));

		//Get username from user
		System.out.print("Enter your name: ");
		name = scanner.nextLine();

		System.out.println("You should now be connected to the server. Feel free to\n" +
							"start messaging now. Message \"exit\" to close the connection.");

		//Start listening to other clients
		Thread inputThread = getInputThread();
		inputThread.start();

		//Listen for messages to send
		while (true) {
			System.out.print("You: ");
			String msgToSend = scanner.nextLine();

			//Send message
			try {
				bw.write(msgToSend);
				bw.newLine();
				bw.flush();
			} catch (IOException e) {
				System.out.println("An IOException Occured!");
				System.out.println("An issue occured when writing your message");
			}

			//Close connection
			if (msgToSend.equalsIgnoreCase("exit")) {
				break;
			}
		}

		//Close scanner
		scanner.close();
	}

	/**
	 * A method that prints input from other clients
	 * @return thread explained above ^
	 */
	public Thread getInputThread(){
		Runnable running = new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						System.out.println(br.readLine());
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		return new Thread(running, "Message Listener");
	}
}
