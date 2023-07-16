import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
	private Socket client;
	private BufferedReader br;
	private BufferedWriter bw;
	
	public ArrayList<ClientHandler> handlersClients;
	public Thread thread;
	public String name;

	public ClientHandler(String name, Socket clientSocket, ArrayList<ClientHandler> clients) throws IOException{
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
				String msgFromClient = br.readLine();
				System.out.println(name + ": " + msgFromClient);
				outToAll(msgFromClient);

				if (msgFromClient.equalsIgnoreCase("exit")){
					break;
				}
			}
			client.close();
			br.close();
			bw.close();
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
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
