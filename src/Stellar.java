import java.io.IOException;
import java.net.UnknownHostException;


public class Stellar {
	public static void main(String[] args) throws IOException {
		//start logs
		Log.start();
		Runtime.getRuntime().addShutdownHook(new Crash());
		//start game
		new Thread(new StateMachine()).start();
		
	}
}
