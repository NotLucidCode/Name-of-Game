import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Receive implements Runnable {
	int port = 3074;
	
	public void receive() throws IOException {
		System.out.println("1");
		ServerSocket ssocket = new ServerSocket(port);
		Socket socket = ssocket.accept();
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
		ssocket.close();
		System.out.println((String)ois.readUTF());
	}

	@Override
	public void run() {
		try {
			receive();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
