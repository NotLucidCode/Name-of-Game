import java.io.IOException;

public class StateMachine implements Runnable {
	/**
	 * 
	 */
	static Render render = new Render();
	static BuildingSaveData bsd = null;
	int state = 0;
	boolean running = false;
	
	public void gameLoop() {
		while(running) { //60hz loop here
			render.state = state;
			System.out.print("");//really important to keep the loop running, again won't be needed later
		}
	}
	public void load(){
		running = true;
		World.loadWorld("WorldNew");
		new Thread(render).start();
		try {
			bsd  = (BuildingSaveData) ResourceManager.loadObject("BuildingSaveData");
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		if(bsd == null) {
			bsd = new BuildingSaveData();
		}
	}
	@Override
	public void run() {
		load();
		gameLoop();
	}
}
