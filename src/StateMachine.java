import java.io.IOException;

public class StateMachine implements Runnable {
	/**
	 * 
	 */
	static Render render = new Render();
	static SaveData sd = null;
	int state = 0;
	static boolean running = false;
	
	public void gameLoop() {
		while(running) { //60hz loop here
			render.state = state;
			System.out.print("");//really important to keep the loop running, again won't be needed later
		}
	}
	
	public static void stop() {
		running = false;
		render.running = false;
		render.frame.dispose();
		sd.world = World.world;
		try {
			ResourceManager.save("SaveData", sd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public void load(){
		running = true;
		new Thread(render).start();
		try {
			sd  = (SaveData) ResourceManager.loadObject("SaveData");
			World.world = sd.world;
		} catch (ClassNotFoundException | IOException e) {
			sd = null;
			//e.printStackTrace();
			//log.add("No SaveData found, loading new");
		}
		if(sd == null) {
			sd = new SaveData();
			World.loadWorld("WorldNew");
		}
		
	}
	@Override
	public void run() {
		load();
		gameLoop();
	}
}
