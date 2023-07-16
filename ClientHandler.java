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
		try {		
			while (true) {
				//Listen to input from client
				String msgFromClient = br.readLine();

				//Print out output from client
				System.out.println(name + ": " + msgFromClient);

				//Gives out output to all clients
				outToAll(msgFromClient);

				//Exit loop if message is exit
				if (msgFromClient.equalsIgnoreCase("exit")){
					break;
				}
			}

			//Close things
			client.close();
			br.close();
			bw.close();
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send a message out to all clients
	 * @param msg Message to send
	 * @throws IOException may be thrown
	 */
	private void outToAll(String msg) throws IOException{
		for (int x = 0; x < handlersClients.size(); x++) {
			
			//Won't send message to itself
			if (handlersClients.get(x).name != name) {
				handlersClients.get(x).bw.write(name + ": " + msg);
				handlersClients.get(x).bw.newLine();
				handlersClients.get(x).bw.flush();	
			}
		}
	}
}
