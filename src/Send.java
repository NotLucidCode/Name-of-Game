import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Send implements Runnable {
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

	@Override
	public void run() {
		try {
			send();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
