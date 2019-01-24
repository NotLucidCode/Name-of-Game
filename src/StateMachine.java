import java.io.IOException;

import javax.swing.JFrame;

public class StateMachine implements Runnable {
	/**
	 * 
	 */
	static Render render = new Render();
	//static AudioFile audio = new AudioFile("./Content/Audio/BackgroundMusic");
	static MusicPlayer music = new MusicPlayer(true);
	static SaveData sd = null;
	int state = 0;
	static boolean running = false;
	static int money = 0, pop = 0, goods = 0, reqPop = 0;
	
	public void gameLoop() {
		while(running) { 
			render.state = state;
			tick();
		}
	}
	
	public static void stop() {
		music.stop();
		running = false;
		render.running = false;
		for(JFrame j : render.JFrames) {
			j.dispose();
		}
		sd.world = World.world;
		sd.rpTable = Tile.buildingSlave.getRpTable();
		try {
			ResourceManager.save("SaveData", sd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public void tick() {
		//one tick of the game has been completed
		System.out.print("");
	}
	
	public void load(){
		running = true;
		new Thread(render).start();
		new Thread(music).start();
		try {
			sd  = (SaveData) ResourceManager.loadObject("SaveData");
			World.world = sd.world;
			Tile.buildingSlave.setRpTable(sd.rpTable);
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
