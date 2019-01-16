import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class Render implements Runnable {
	boolean isInit = false;
	int camX = 0;
	int camY = 0;
	int sideLength = 20;
	int height = (int) (sideLength * Math.sin(Math.PI / 3));// this is the formula to find the height of a equilateral
															// triangle thanks google!
	int step = 10;
	JFrame frame = new JFrame("Render Test");
	Canvas canvas = new Canvas();
	BufferStrategy bs = null;
	boolean running = true;
	int WIDTH = /**1920;*/  Toolkit.getDefaultToolkit().getScreenSize().width;
	int HEIGHT = /**1080;*/Toolkit.getDefaultToolkit().getScreenSize().height;
	GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[1];
	Graphics g = null;
	Tile tileMode = Tile.land;
	static volatile ArrayList<BuildingMenu> buildingMenuList = new ArrayList<BuildingMenu>();
	volatile HashMap<Polygon, Tile> buttonMap = new HashMap<Polygon, Tile>();
	volatile HashMap<Polygon, Integer> tabMap = new HashMap<Polygon, Integer>();
	volatile HashMap<Polygon, Integer> buildingSizeMap =  new HashMap<Polygon, Integer>();
	int tabState = 0;
	int currentBuildingSize = 0;
	boolean dragging = false;
	BuildingMenu bMBeingDragged = null;
	int state = 0;
	
	public void renderLoop() {
		while(running) {
			try {
				g = bs.getDrawGraphics();
				if(state == 0) { //game state, display the world and everything that goes along with it			
					renderWorld(g);
					renderButtons(g);
					renderBuildingMenu(g);
				}
				bs.show();
			} catch (Exception e) {
				//log.add("Unable to render frame");
			}
		}
	}
	
	public void renderWorld(Graphics g) {
		
		try {
			for (int x = 0; x < (int) World.world.get(0).size() * 2 + 2; x++) {
				for (int y = 0; y < (int) World.world.size() + 2; y++) {
					if (y < World.world.size() && x < World.world.get(y).size()) {
						g.setColor(World.world.get(y).get(x).getColor());
					} else {
						g.setColor(Color.BLACK);
					}
					int pixelX = (x - (int) x / 2) * height + camX;
					int pixelY = (x - (int) x / 2) % 2 == 0 ? y * sideLength + camY
							: y * sideLength + camY - sideLength / 2;
					g.fillPolygon(new int[] { pixelX, pixelX, x % 2 == 0 ? pixelX + height : pixelX - height },
							new int[] { pixelY, pixelY + sideLength, pixelY + sideLength / 2 }, 3);
	
				}
			}	
		}catch(Exception e) {
			
		}
	}

	public void setupFrame() {
		
		running = true;
		frame.setVisible(true);
		canvas.setVisible(true);
		if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
			Dimension dimension = new Dimension(WIDTH, HEIGHT);
			frame.setMaximumSize(dimension);
			frame.setMinimumSize(dimension);
			frame.setPreferredSize(dimension);
			frame.setSize(dimension);
		}
		if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0) {
			device.setFullScreenWindow(frame);
		}
		canvas.setSize(frame.getWidth(), frame.getHeight());
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.add(canvas);
		canvas.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mousePressed(MouseEvent e) {
				boolean handled = false;
				int i = 0;
				for (Polygon p : buttonMap.keySet()) {
					if (p.contains(e.getPoint())) {
						tileMode = buttonMap.get(p);
						handled = true;
						if(buttonMap.get(p).equals(Tile.building)) {
							currentBuildingSize = buildingSizeMap.get(p);
						}
					}
					i++;
				}
				
				for (Polygon p : tabMap.keySet()) {
					if (p.contains(e.getPoint())) {
						tabState = tabMap.get(p);
						handled = true;
					}
				}
				try {
					for(BuildingMenu bM : buildingMenuList) {
						if((e.getX() > bM.getX() && e.getX() < bM.getSizeX() + bM.getX()) && (e.getY() > bM.getY() && e.getY() < bM.getSizeY() + bM.getY())) {
							bM.click(new Point(e.getX(), e.getY()));
							handled = true;
						}
					}
				}catch(Exception ex) {
					ex.printStackTrace();
					handled = true;
				}
				if (!handled) {
					int x = (int) Math.round((e.getX() - camX) * 2 / (height));
					int y = (int) Math.round((e.getY() - camY) / sideLength);
					if (World.world.get(y).get(x).equals(Tile.building) || World.world.get(y).get(x).equals(Tile.buildingSlave)) {
						Point p = new Point(x,y);
						if (World.world.get(y).get(x).equals(Tile.buildingSlave)) {
							p = Tile.buildingSlave.getRpTable().get(new Point(x,y));
						}
						for(Building b : StateMachine.sd.buildings) {
							if(b.getX() == p.x && b.getY() == p.y) {
								b.click(p);
								break;
							}
						}
					} else if (tileMode == Tile.building) {
						new Building(x % 2 == 0 ? x - 1 : x, y, currentBuildingSize).create();
					} else {
						World.world.get(y).set(x, tileMode);
					}

				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				dragging = false;
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		canvas.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				boolean handled = false;
				int x = (int) (e.getX() - camX) * 2 / (height);
				int y = (int) (e.getY() - camY) / sideLength;
				if(!dragging) {
					for(BuildingMenu bM : buildingMenuList) {
						if((e.getX() > bM.getX() && e.getX() < bM.getSizeX() + bM.getX()) && (e.getY() > bM.getY() && e.getY() < bM.getSizeY() + bM.getY())) {
							bM.drag(new Point(e.getX(), e.getY()));
							bMBeingDragged = bM;
							handled = true;
							dragging = true;
						}
					}
					if (!handled) {
						if (tileMode != Tile.building) {
							World.world.get(y).set(x, tileMode);
							World.world.get(y -1).set(x, tileMode);
							World.world.get(y + 1).set(x, tileMode);
							World.world.get(y).set(x + 1, tileMode);
							World.world.get(y).set(x - 1, tileMode);
						}
					}
				} else {
					bMBeingDragged.drag(new Point(e.getX(), e.getY()));
				}
			} 

			@Override
			public void mouseMoved(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		canvas.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.getWheelRotation() > 0 && height > 1) {
					sideLength--;
					height = (int) (sideLength * Math.sin(Math.PI / 3));
				} else if (e.getWheelRotation() < 0) {
					sideLength++;
					height = (int) (sideLength * Math.sin(Math.PI / 3));
				}

			}

		});
		canvas.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 87) {// W
					camY -= step;
				} else if (e.getKeyCode() == 83) {// S
					camY += step;
				} else if (e.getKeyCode() == 65) {// A
					camX -= step;
				} else if (e.getKeyCode() == 68) {// D
					camX += step;
				} else if (e.getKeyCode() == 27) {// esc
					try {				
						StateMachine.stop();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
//					System.exit(0);
				} else if(e.getKeyCode() == 49) {
					currentBuildingSize = 0;
				} else if(e.getKeyCode() == 50) {
					currentBuildingSize = 1;
				} else if(e.getKeyCode() == 51) {
					currentBuildingSize = 2;
				} else if(e.getKeyCode() == 52) {
					currentBuildingSize = 3;
				} else if(e.getKeyCode() == 53) {
					currentBuildingSize = 4;
				}

			}
		});
		canvas.createBufferStrategy(3);
		canvas.requestFocus();
		bs = canvas.getBufferStrategy();
		
		tabMap.put(new Polygon(new int[] {0 * (WIDTH / 32) + WIDTH / 32, 2 * WIDTH / 32, 2 * WIDTH / 32, 0 * (WIDTH / 32) + WIDTH / 32}, new int[] {HEIGHT- HEIGHT / 8, HEIGHT - HEIGHT / 8, HEIGHT - HEIGHT / 16 , HEIGHT - HEIGHT / 16}, 4),0);
		tabMap.put(new Polygon(new int[] {1 * (WIDTH / 32) + WIDTH / 32 + 50, 3 * WIDTH / 32 + 50, 3 * WIDTH / 32 + 50, 1 * (WIDTH / 32) + WIDTH / 32 + 50}, new int[] {HEIGHT- HEIGHT / 8, HEIGHT - HEIGHT / 8, HEIGHT - HEIGHT / 16 , HEIGHT - HEIGHT / 16}, 4),1);
		tabMap.put(new Polygon(new int[] {2 * (WIDTH / 32) + WIDTH / 32 + 100, 4 * WIDTH / 32 + 100, 4 * WIDTH / 32 + 100, 2 * (WIDTH / 32) + WIDTH / 32 + 100}, new int[] {HEIGHT- HEIGHT / 8, HEIGHT - HEIGHT / 8, HEIGHT - HEIGHT / 16 , HEIGHT - HEIGHT / 16}, 4),2);
		tabMap.put(new Polygon(new int[] {3 * (WIDTH / 32) + WIDTH / 32 + 150, 5 * WIDTH / 32 + 150, 5 * WIDTH / 32 + 150, 3 * (WIDTH / 32) + WIDTH / 32 + 150}, new int[] {HEIGHT- HEIGHT / 8, HEIGHT - HEIGHT / 8, HEIGHT - HEIGHT / 16 , HEIGHT - HEIGHT / 16}, 4),3);
		
		isInit = true;

	}

	private void renderButtons(Graphics g) {
		buttonMap =  new HashMap<Polygon, Tile>();
		buildingSizeMap =  new HashMap<Polygon, Integer>();
		g.setColor(Color.DARK_GRAY);
		for (Polygon p : tabMap.keySet()) {
			g.fillPolygon(p);
		}
		int startX = (int) WIDTH / 16;
		int buttonSize = 80;
		int startY = (int) HEIGHT - buttonSize;
		if(tabState == 0) {
			
			g.setColor(Color.RED);
			Polygon landButton = new Polygon(new int[] { startX, startX, startX + buttonSize, startX + buttonSize },
					new int[] { startY, startY + buttonSize, startY + buttonSize, startY }, 4);
			buttonMap.put(landButton, Tile.land);
			g.fillPolygon(landButton);
	
			g.setColor(Color.CYAN);
			startX = (int) 2 * (WIDTH / 16);
			buttonSize = 80;
			startY = (int) HEIGHT - buttonSize;
	
			Polygon waterButton = new Polygon(new int[] { startX, startX, startX + buttonSize, startX + buttonSize },
					new int[] { startY, startY + buttonSize, startY + buttonSize, startY }, 4);
			buttonMap.put(waterButton, Tile.water);
			g.fillPolygon(waterButton);
	
			g.setColor(Color.DARK_GRAY);
			startX = (int) 3 * (WIDTH / 16);
			buttonSize = 80;
			startY = (int) HEIGHT - buttonSize;
	
			Polygon edgeButton = new Polygon(new int[] { startX, startX, startX + buttonSize, startX + buttonSize },
					new int[] { startY, startY + buttonSize, startY + buttonSize, startY }, 4);
			buttonMap.put(edgeButton, Tile.edge);
			g.fillPolygon(edgeButton);
	
			g.setColor(new Color(194,178,128));
			startX = (int) 4 * (WIDTH / 16);
			buttonSize = 80;
			startY = (int) HEIGHT - buttonSize;
	
			Polygon sandButton = new Polygon(new int[] { startX, startX, startX + buttonSize, startX + buttonSize },
					new int[] { startY, startY + buttonSize, startY + buttonSize, startY }, 4);
			buttonMap.put(sandButton, Tile.sand);
			g.fillPolygon(sandButton);	
		} else if (tabState == 1) {
			g.setColor(Color.DARK_GRAY);
			Polygon size0 = new Polygon(new int[] { startX, startX, startX + buttonSize, startX + buttonSize },
					new int[] { startY, startY + buttonSize, startY + buttonSize, startY }, 4);
			buttonMap.put(size0, Tile.building);
			buildingSizeMap.put(size0, 0);
			g.fillPolygon(size0);
	
			g.setColor(Color.DARK_GRAY);
			startX = (int) 2 * (WIDTH / 16);
			buttonSize = 80;
			startY = (int) HEIGHT - buttonSize;
	
			Polygon size1 = new Polygon(new int[] { startX, startX, startX + buttonSize, startX + buttonSize },
					new int[] { startY, startY + buttonSize, startY + buttonSize, startY }, 4);
			buttonMap.put(size1, Tile.building);
			buildingSizeMap.put(size1, 1);
			g.fillPolygon(size1);
	
			g.setColor(Color.DARK_GRAY);
			startX = (int) 3 * (WIDTH / 16);
			buttonSize = 80;
			startY = (int) HEIGHT - buttonSize;
	
			Polygon size2 = new Polygon(new int[] { startX, startX, startX + buttonSize, startX + buttonSize },
					new int[] { startY, startY + buttonSize, startY + buttonSize, startY }, 4);
			buttonMap.put(size2, Tile.building);
			buildingSizeMap.put(size2, 2);
			g.fillPolygon(size2);
	
			g.setColor(Color.DARK_GRAY);
			startX = (int) 4 * (WIDTH / 16);
			buttonSize = 80;
			startY = (int) HEIGHT - buttonSize;
	
			Polygon size3 = new Polygon(new int[] { startX, startX, startX + buttonSize, startX + buttonSize },
					new int[] { startY, startY + buttonSize, startY + buttonSize, startY }, 4);
			buttonMap.put(size3, Tile.building);
			buildingSizeMap.put(size3, 3);
			g.fillPolygon(size3);
			
			g.setColor(Color.DARK_GRAY);
			startX = (int) 5 * (WIDTH / 16);
			buttonSize = 80;
			startY = (int) HEIGHT - buttonSize;
	
			Polygon size4 = new Polygon(new int[] { startX, startX, startX + buttonSize, startX + buttonSize },
					new int[] { startY, startY + buttonSize, startY + buttonSize, startY }, 4);
			buttonMap.put(size4, Tile.building);
			buildingSizeMap.put(size4, 4);
			g.fillPolygon(size4);
		}
	}

	private void renderBuildingMenu(Graphics g) {
		try {
			for (BuildingMenu b : buildingMenuList) {
				b.draw(g);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		setupFrame();
		renderLoop();
	}
}
