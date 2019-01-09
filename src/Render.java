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

import java.io.IOException;

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
	GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	Graphics g = null;
	Tile tileMode = Tile.land;
	static volatile ArrayList<BuildingMenu> buildingMenuList = new ArrayList<BuildingMenu>();
	volatile HashMap<Polygon, Tile> buttonMap = new HashMap<Polygon, Tile>();
	int currentBuildingSize = 0;
	boolean dragging = false;
	BuildingMenu bMBeingDragged = null;
	
	public void renderWorld() {

		g = bs.getDrawGraphics();
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
		renderButtons(g);
		renderBuildingMenu(g);
		bs.show();
	}

	public void setupFrame() {

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
				for (Polygon p : buttonMap.keySet()) {
					if (p.contains(e.getPoint())) {
						tileMode = buttonMap.get(p);
						handled = true;
					}
				}
				for(BuildingMenu bM : buildingMenuList) {
					if((e.getX() > bM.getX() && e.getX() < bM.getSizeX() + bM.getX()) && (e.getY() > bM.getY() && e.getY() < bM.getSizeY() + bM.getY())) {
						bM.click(new Point(e.getX(), e.getY()));
						handled = true;
					}
				}
				if (!handled) {
					//System.out.println(
					//		"Click at " + (e.getX() + camX) * 2 / (height) + ", " + (e.getY() + camY) / sideLength);
					int x = (int) Math.round((e.getX() - camX) * 2 / (height));
					int y = (int) Math.round((e.getY() - camY) / sideLength);
					if (World.world.get(y).get(x).equals(Tile.building) || World.world.get(y).get(x).equals(Tile.buildingSlave)) {
						Point p = new Point(x,y);
						if (World.world.get(y).get(x).equals(Tile.buildingSlave)) {
							p = Tile.buildingSlave.getRpTable().get(new Point(x,y));
						}
						for(Building b : StateMachine.bsd.buildings) {
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
						ResourceManager.save("BuildingSaveData", StateMachine.bsd);
						World.saveWorld();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					System.exit(0);
				} else if(e.getKeyCode() == 49) {
					currentBuildingSize = 0;
				} else if(e.getKeyCode() == 50) {
					currentBuildingSize = 1;
				}

			}
		});
		canvas.createBufferStrategy(3);
		canvas.requestFocus();
		bs = canvas.getBufferStrategy();
		isInit = true;

	}

	private void renderButtons(Graphics g) {
		
		g.setColor(Color.RED);
		int startX = (int) WIDTH / 8;
		int buttonSize = 80;
		int startY = (int) HEIGHT - buttonSize;

		Polygon landButton = new Polygon(new int[] { startX, startX, startX + buttonSize, startX + buttonSize },
				new int[] { startY, startY + buttonSize, startY + buttonSize, startY }, 4);
		buttonMap.put(landButton, Tile.land);
		g.fillPolygon(landButton);

		g.setColor(Color.CYAN);
		startX = (int) 2 * (WIDTH / 8);
		buttonSize = 80;
		startY = (int) HEIGHT - buttonSize;

		Polygon waterButton = new Polygon(new int[] { startX, startX, startX + buttonSize, startX + buttonSize },
				new int[] { startY, startY + buttonSize, startY + buttonSize, startY }, 4);
		buttonMap.put(waterButton, Tile.water);
		g.fillPolygon(waterButton);

		g.setColor(Color.DARK_GRAY);
		startX = (int) 3 * (WIDTH / 8);
		buttonSize = 80;
		startY = (int) HEIGHT - buttonSize;

		Polygon edgeButton = new Polygon(new int[] { startX, startX, startX + buttonSize, startX + buttonSize },
				new int[] { startY, startY + buttonSize, startY + buttonSize, startY }, 4);
		buttonMap.put(edgeButton, Tile.edge);
		g.fillPolygon(edgeButton);

		g.setColor(Color.DARK_GRAY);
		startX = (int) 4 * (WIDTH / 8);
		buttonSize = 80;
		startY = (int) HEIGHT - buttonSize;

		Polygon buildingButton = new Polygon(new int[] { startX, startX, startX + buttonSize, startX + buttonSize },
				new int[] { startY, startY + buttonSize, startY + buttonSize, startY }, 4);
		buttonMap.put(buildingButton, Tile.building);
		g.fillPolygon(buildingButton);
	}

	private void renderBuildingMenu(Graphics g) {
		for (BuildingMenu b : buildingMenuList) {
			b.draw(g);
		}
	}
	
	@Override
	public void run() {
		setupFrame();
	}
}
