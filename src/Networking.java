import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Networking implements Runnable  {
	//String ip = "24.155.103.60";
	

	int port = 3074;
	
	public void send() throws UnknownHostException, IOException {
		System.out.println("0");
		InetAddress addr = InetAddress.getByName("127.0.0.1");//24.155.103.60");
		Socket socket = new Socket(addr, port);
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeUTF("hello world");
		oos.flush();
		oos.close();
		socket.close();
	}
	public void recieve() throws IOException {
		System.out.println("1");
		ServerSocket ssocket = new ServerSocket(port);
		Socket socket = ssocket.accept();
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
		ssocket.close();
		socket.close();
		System.out.println((String)ois.readUTF());
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
