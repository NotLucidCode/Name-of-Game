import java.io.FileNotFoundException;
import java.io.IOException;

public class StateMachine implements Runnable {
	/**
	 * 
	 */
	Render render = new Render();
	static BuildingSaveData bsd = null;
	
	public void gameLoop() {
		while(true) { //60hz loop here
			if (render.isInit && render.running) {
				render.renderWorld();
			} else if(!render.isInit){
				System.out.print("");//really important to keep the loop running, again won't be needed later
			}
		}
	}
	public void load(){
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
