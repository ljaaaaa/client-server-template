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
		System.out.println("╔═════════════════════════════╗");
		System.out.println("║  Welcome to being a Client! ║")
		System.out.println("╚═════════════════════════════╝");
		System.out.println();

		//Initialize socket and streams
		try {
			socket = new Socket(HOST, PORT);
			outStream = socket.getOutputStream();
			inStream = socket.getInputStream();

		} catch (IOException e) {
			System.out.println("An IOException Occured!");
			System.out.println("Check: Is the server running? Are your HOST and PORT variables correct?\n");
			System.exit(1);
		}

		//Get username from user
		System.out.print("Enter your name: ");
		name = scanner.nextLine();

		//Initialize readers and writers
		br = new BufferedReader(new InputStreamReader(inStream));
		bw = new BufferedWriter(new OutputStreamWriter(outStream));

		//Send username to clienthandler to process
		sendMessage(name);

		System.out.println("\nYou should now be connected to the server. Feel free to\n" +
							"start messaging now. Press Ctrl+C to close the connection.\n");

		//Start listening to other clients
		Thread inputThread = getInputThread();
		inputThread.start();

		//Listen for messages to send
		while (true) {
			String msgToSend = scanner.nextLine();
			System.out.println();
			
			//Send message
			sendMessage(msgToSend);
		}
	}

	/**
	 * Send a message to the server (via ClientHandler) which passes message
	 * to every other client
	 * @param msgToSend the message to send
	 */
	public void sendMessage(String msgToSend){
		try {
			bw.write(msgToSend);
			bw.newLine();
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * A method that prints input from other clients
	 * @return thread explained above ^
	 */
	public Thread getInputThread(){
		Runnable running = new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						String text = br.readLine();
						//If server has been shut down, input will be null
						if (text == null){
							System.out.println("It appears the server has shut down!");
							System.exit(1);

						//Otherwise print input normally
						} else {
							System.out.println(text);
							System.out.println();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};

		return new Thread(running, "Message Listener");
	}
}
