import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	
	public static Socket socket;
	public static BufferedReader br;
	public static BufferedWriter bw;
	public static Thread thread;
	
	public static void main(String[] args) {
		socket = null;
		br = null;
		bw = null;
		
		try {
			socket = new Socket("localhost", 1234);
			
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			Scanner scanner = new Scanner(System.in);
			
			//Listens for messages and prints them here
			Runnable running = new Runnable() {
				@Override
				public void run() {
					try {
						while (true) {
							System.out.println(br.readLine());
						}
					}
					
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			};

			thread = new Thread(running, "Message Listener");
			thread.start();
			
			while (true) {
				String msgToSend = scanner.nextLine();
				bw.write(msgToSend);
				bw.newLine();
				bw.flush();
				
				if (msgToSend.equalsIgnoreCase("exit")) {
					break;
				}
			}
		}
		
		catch (Exception e) {
			e.printStackTrace();
		}
		
		finally {
			try {
				if (socket != null) {
					socket.close();
				}
				if (br != null) {
					br.close();
				}
				if (bw != null) {
					bw.close();
				}
			}
			
			catch (Exception e){
				e.printStackTrace();
			}
		}
	}
}
