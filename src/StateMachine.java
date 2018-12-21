
public class StateMachine implements Runnable {
	/**
	 * 
	 */
	Render render = new Render();
	
	public void gameLoop() {
		while(true) { //60hz loop here
			if (render.isInit) {
				render.renderWorld();
			}
			System.out.println("");//really important to keep the loop running, again won't be needed later
		}
	}
	public void load() {
		World.loadWorld("World");
		new Thread(render).start();
	}
	@Override
	public void run() {
		load();
		gameLoop();
	}
}
