import java.io.IOException;
import java.net.UnknownHostException;

public class Stellar {
	public static void main(String[] args) throws IOException {
		//start logs
		//start game
		new Thread(new StateMachine()).start();
		//new Thread(new Receive()).start();
		//new Thread(new Send()).start();
		
	}
}
