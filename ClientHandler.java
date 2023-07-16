import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * ClientHandler Class
 * A handler for a specific client
 * Lives in a separate thread (hence why implements Runnable)
 */
public class ClientHandler implements Runnable {
	private Socket client;
	private BufferedReader br;
	private BufferedWriter bw;
	
	public ArrayList<ClientHandler> handlersClients;
	public Thread thread;
	public String name;

	private int numMessages = 0;

	public ClientHandler(String name, Socket clientSocket, ArrayList<ClientHandler> clients) throws IOException {
		//Initializes global variables
		this.client = clientSocket;
		this.handlersClients = clients;
		this.name = name;
		br = new BufferedReader(new InputStreamReader(client.getInputStream()));
		bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));	
		thread = new Thread(this, name);
	}

	@Override
	public void run() {		
		//Loop continuously
		while (true) {
			//Listen to input from client
			String msgFromClient = null;

			//Get message from client
			try {
				msgFromClient = br.readLine();

			} catch (IOException e) {
				e.printStackTrace();
			}
			numMessages++;

			//If first message, set username to whatever is sent from client,
			//alongside notifying everyone about new user
			if (numMessages == 1){
				name = msgFromClient;
				System.out.println(name + " entered the chat!");
				outToAll(name + " entered the chat!");
			
			//If not first message, just send messages as normal
			} else {
				//Print out output from client
				System.out.println(name + ": " + msgFromClient);

				//Gives out output to all clients
				outToAll(msgFromClient);

				//Exit loop if message is exit
				if (msgFromClient.equalsIgnoreCase("exit")){
					break;
				}
			} 
		}

		//Close things
		try {
			client.close();
			br.close();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send a message out to all clients
	 * @param msg Message to send
	 */
	private void outToAll(String msg){
		for (int x = 0; x < handlersClients.size(); x++) {
			
			//Won't send message to itself
			if (handlersClients.get(x).name != name) {
				try {
					handlersClients.get(x).bw.write(name + ": " + msg);
					handlersClients.get(x).bw.newLine();
					handlersClients.get(x).bw.flush();
				} catch (IOException e) {
					
				}	
			}
		}
	}
}
