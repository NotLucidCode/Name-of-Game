import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
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
															// triangle
	int step = 10;
	ArrayList<JFrame> JFrames = new ArrayList<JFrame>();
	ArrayList<BufferStrategy> BSList = new ArrayList<BufferStrategy>();
	boolean running = true;
	int WIDTH = /** 1920; */
			Toolkit.getDefaultToolkit().getScreenSize().width;
	int HEIGHT = /** 1080; */
			Toolkit.getDefaultToolkit().getScreenSize().height;
	GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	Graphics g = null;
	Tile tileMode = Tile.land;
	static volatile ArrayList<BuildingMenu> buildingMenuList = new ArrayList<BuildingMenu>();
	volatile HashMap<Polygon, Tile> buttonMap = new HashMap<Polygon, Tile>();
	volatile HashMap<Polygon, Integer> tabMap = new HashMap<Polygon, Integer>();
	{
		tabMap.put(new Polygon(
				new int[] { 4 * (WIDTH / 32) + WIDTH / 32, 6 * WIDTH / 32, 6 * WIDTH / 32,
						4 * (WIDTH / 32) + WIDTH / 32 },
				new int[] { HEIGHT - HEIGHT / 8, HEIGHT - HEIGHT / 8, HEIGHT - HEIGHT / 16, HEIGHT - HEIGHT / 16 }, 4),
				0);
		tabMap.put(new Polygon(
				new int[] { 5 * (WIDTH / 32) + WIDTH / 32 + 50, 7 * WIDTH / 32 + 50, 7 * WIDTH / 32 + 50,
						5 * (WIDTH / 32) + WIDTH / 32 + 50 },
				new int[] { HEIGHT - HEIGHT / 8, HEIGHT - HEIGHT / 8, HEIGHT - HEIGHT / 16, HEIGHT - HEIGHT / 16 }, 4),
				1);
		tabMap.put(new Polygon(
				new int[] { 6 * (WIDTH / 32) + WIDTH / 32 + 100, 8 * WIDTH / 32 + 100, 8 * WIDTH / 32 + 100,
						6 * (WIDTH / 32) + WIDTH / 32 + 100 },
				new int[] { HEIGHT - HEIGHT / 8, HEIGHT - HEIGHT / 8, HEIGHT - HEIGHT / 16, HEIGHT - HEIGHT / 16 }, 4),
				2);
		tabMap.put(new Polygon(
				new int[] { 7 * (WIDTH / 32) + WIDTH / 32 + 150, 9 * WIDTH / 32 + 150, 9 * WIDTH / 32 + 150,
						7 * (WIDTH / 32) + WIDTH / 32 + 150 },
				new int[] { HEIGHT - HEIGHT / 8, HEIGHT - HEIGHT / 8, HEIGHT - HEIGHT / 16, HEIGHT - HEIGHT / 16 }, 4),
				3);
	}
	volatile HashMap<Polygon, Integer> buildingSizeMap = new HashMap<Polygon, Integer>();
	int tabState = 0;
	int currentBuildingSize = 0;
	boolean dragging = false;
	BuildingMenu bMBeingDragged = null;
	int state = 0;
	int currentFrame = 0;
	boolean exitKeyPushed = false;

	public void renderLoop() {
		while (running) {
			try {
				for (BufferStrategy bs : BSList) {
					try {
						g = bs.getDrawGraphics();
						if (state == 0) { // game state, display the world and everything that goes along with it
							renderWorld(g);
							renderButtons(g);
							renderBuildingMenu(g);
						}
						bs.show();
					} catch (Exception e1) {
						try {
							String s = "";
							e1.printStackTrace(new PrintWriter(s));
							Log.add(s);
						} catch (IOException e2) {
							/**
							 * In this case the error was unable to be added to the log, throwing an error.
							 * Because the new error was adding the original error to the log, we cannot add
							 * errors to the log for some reason so the game moves on.
							 */
						}
					}
				}
			} catch (Exception e1) {
				try {
					String s = "";
					e1.printStackTrace(new PrintWriter(s));
					Log.add(s);
				} catch (IOException e2) {
					/**
					 * In this case the error was unable to be added to the log, throwing an error.
					 * Because the new error was adding the original error to the log, we cannot add
					 * errors to the log for some reason so the game moves on.
					 */
				}
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
		} catch (Exception e1) {
			try {
				String s = "";
				e1.printStackTrace(new PrintWriter(s));
				Log.add(s);
			} catch (IOException e2) {
				/**
				 * In this case the error was unable to be added to the log, throwing an error.
				 * Because the new error was adding the original error to the log, we cannot add
				 * errors to the log for some reason so the game moves on.
				 */
			}
		}
	}

	/**
	 * As much info as is possible
	 */
	public void setupFrame(int i) {
		JFrames.add(new JFrame("Stellar"));
		Canvas canvas = new Canvas();
		running = true;
		JFrames.get(i).setVisible(true);
		canvas.setVisible(true);
		if (i == 0) {
			if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
				Dimension dimension = new Dimension(WIDTH, HEIGHT);
				JFrames.get(i).setMaximumSize(dimension);
				JFrames.get(i).setMinimumSize(dimension);
				JFrames.get(i).setPreferredSize(dimension);
				JFrames.get(i).setSize(dimension);
			}
			// insight, like: this is used to make the game run better on mac
			if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0) {
				if (device.isFullScreenSupported()) {
					device.setFullScreenWindow(JFrames.get(i));
				}
			}
		} else {
			Dimension dimension = new Dimension(WIDTH / (i + 1), HEIGHT / (i + 1));
			if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0) {
				if (device.isFullScreenSupported()) {
					device.setFullScreenWindow(null);
				}
			}
			for (JFrame j : JFrames) {
				j.setMaximumSize(null);
				j.setMinimumSize(null);
				j.setSize(dimension);
			}
		}
		canvas.setSize(JFrames.get(i).getWidth(), JFrames.get(i).getHeight());
		JFrames.get(i).setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		JFrames.get(i).add(canvas);
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
						if (buttonMap.get(p).equals(Tile.building)) {
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
					for (BuildingMenu bM : buildingMenuList) {
						if ((e.getX() > bM.getX() && e.getX() < bM.getSizeX() + bM.getX())
								&& (e.getY() > bM.getY() && e.getY() < bM.getSizeY() + bM.getY())) {
							bM.click(new Point(e.getX(), e.getY()));
							handled = true;
						}
					}
				} catch (Exception e1) {
					try {
						String s = "";
						e1.printStackTrace(new PrintWriter(s));
						Log.add(s);
					} catch (IOException e2) {
						/**
						 * In this case the error was unable to be added to the log, throwing an error.
						 * Because the new error was adding the original error to the log, we cannot add
						 * errors to the log for some reason so the game moves on.
						 */
					}
				}

				if (!handled) {
					int x = (int) Math.round((e.getX() - camX) * 2 / (height));
					int y = (int) Math.round((e.getY() - camY) / sideLength);
					if (World.world.get(y).get(x).equals(Tile.building)
							|| World.world.get(y).get(x).equals(Tile.buildingSlave)) {
						Point p = new Point(x, y);
						if (World.world.get(y).get(x).equals(Tile.buildingSlave)) {
							p = Tile.buildingSlave.getRpTable().get(new Point(x, y));
						}
						try {
							for (Building b : StateMachine.sd.buildings) {
								if (b.getX() == p.x && b.getY() == p.y) {
									b.click(p);
									break;
								}
							}
						} catch (Exception e1) {
							try {
								String s = "";
								e1.printStackTrace(new PrintWriter(s));
								Log.add(s);
							} catch (IOException e2) {
								/**
								 * In this case the error was unable to be added to the log, throwing an error.
								 * Because the new error was adding the original error to the log, we cannot add
								 * errors to the log for some reason so the game moves on.
								 */
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
				if (!dragging) {
					for (BuildingMenu bM : buildingMenuList) {
						if ((e.getX() > bM.getX() && e.getX() < bM.getSizeX() + bM.getX())
								&& (e.getY() > bM.getY() && e.getY() < bM.getSizeY() + bM.getY())) {
							bM.drag(new Point(e.getX(), e.getY()));
							bMBeingDragged = bM;
							handled = true;
							dragging = true;
						}
					}
					if (!handled) {
						if (tileMode != Tile.building) {
							World.world.get(y).set(x, tileMode);
							World.world.get(y - 1).set(x, tileMode);
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
				exitKeyPushed = false;
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
						Log.close();
						StateMachine.stop();
					} catch (Exception e1) {
						try {
							String s = "";
							e1.printStackTrace(new PrintWriter(s));
							Log.add(s);
						} catch (IOException e2) {
							/**
							 * In this case the error was unable to be added to the log, throwing an error.
							 * Because the new error was adding the original error to the log, we cannot add
							 * errors to the log for some reason so the game moves on.
							 */
						}
					}
				} else if (e.getKeyCode() == 49) {
					currentBuildingSize = 0;
				} else if (e.getKeyCode() == 50) {
					currentBuildingSize = 1;
				} else if (e.getKeyCode() == 51) {
					currentBuildingSize = 2;
				} else if (e.getKeyCode() == 52) {
					currentBuildingSize = 3;
				} else if (e.getKeyCode() == 53) {
					currentBuildingSize = 4;
				} else if (e.getKeyCode() == 78) {
					setupFrame(currentFrame);
					currentFrame++;
					/**
					 * Our program doesn't use the command key or alt key for anything so if someone
					 * is pressing one of these keys then it is likely they are trying to stop the
					 * program out of its regular course. Because it cannot be determined as of yet
					 * if the programmed was stopped because of a bug or for any other myriad of
					 * reasons the crash reporter is stopped before it can do anything.
					 */
				} else if (e.getKeyCode() == 157 /* cmd key */
						|| e.getKeyCode() == 18 /* alt key */) {
					/**
					 * This can be used to initiate a crash report for testing purposes
					 */
					//new Crash().run();
					exitKeyPushed = true;

				}

			}
		});
		canvas.createBufferStrategy(3);
		canvas.requestFocus();
		BSList.add(canvas.getBufferStrategy());

		JFrames.get(i).add(canvas);

		isInit = true;
	}

	private void renderButtons(Graphics g) {
		g.setFont(new Font("sansserif", Font.BOLD, 32));
		g.setColor(Color.WHITE);
		g.drawString("Money: " + StateMachine.money, 20, HEIGHT - HEIGHT / 8);
		g.drawString("Population: " + StateMachine.pop, 20, HEIGHT - HEIGHT / 8 + 40);
		g.drawString("Goods " + StateMachine.goods, 20, HEIGHT - HEIGHT / 8 + 80);
		
		buttonMap = new HashMap<Polygon, Tile>();
		buildingSizeMap = new HashMap<Polygon, Integer>();
		g.setColor(Color.DARK_GRAY);
		for (Polygon p : tabMap.keySet()) {
			g.fillPolygon(p);
		}
		int startX = (int) WIDTH / 6;
		int buttonSize = 80;
		int startY = (int) HEIGHT - buttonSize;
		if (tabState == 0) {

			g.setColor(new Color(0,80,0));
			Polygon landButton = new Polygon(new int[] { startX, startX, startX + buttonSize, startX + buttonSize },
					new int[] { startY, startY + buttonSize, startY + buttonSize, startY }, 4);
			buttonMap.put(landButton, Tile.land);
			g.fillPolygon(landButton);
			g.setColor(Color.WHITE);
			g.setFont(new Font("sansserif", Font.BOLD, 20));
			g.drawString("Grass", startX, startY + 20);
			
			g.setColor(Color.CYAN);
			startX = (int) startX + (2 * buttonSize) + 20;
			buttonSize = 80;
			startY = (int) HEIGHT - buttonSize;

			Polygon waterButton = new Polygon(new int[] { startX, startX, startX + buttonSize, startX + buttonSize },
					new int[] { startY, startY + buttonSize, startY + buttonSize, startY }, 4);
			buttonMap.put(waterButton, Tile.water);
			g.fillPolygon(waterButton);
			g.setColor(Color.WHITE);
			g.drawString("Water", startX, startY + 20);

			g.setColor(Color.DARK_GRAY);
			startX = (int) startX + (2 * buttonSize) + 20;
			buttonSize = 80;
			startY = (int) HEIGHT - buttonSize;

			Polygon edgeButton = new Polygon(new int[] { startX, startX, startX + buttonSize, startX + buttonSize },
					new int[] { startY, startY + buttonSize, startY + buttonSize, startY }, 4);
			buttonMap.put(edgeButton, Tile.edge);
			g.fillPolygon(edgeButton);
			g.setColor(Color.WHITE);
			g.drawString("Void", startX, startY + 20);

			g.setColor(new Color(194, 178, 128));
			startX = (int) startX + (2 * buttonSize) + 20;
			buttonSize = 80;
			startY = (int) HEIGHT - buttonSize;

			Polygon sandButton = new Polygon(new int[] { startX, startX, startX + buttonSize, startX + buttonSize },
					new int[] { startY, startY + buttonSize, startY + buttonSize, startY }, 4);
			buttonMap.put(sandButton, Tile.sand);
			g.fillPolygon(sandButton);
			g.setColor(Color.WHITE);
			g.setFont(new Font("sansserif", Font.BOLD, 20));
			g.drawString("Sand", startX, startY + 20);
		} else if (tabState == 1) {
			g.setColor(Color.DARK_GRAY);
			Polygon size0 = new Polygon(new int[] { startX, startX, startX + buttonSize, startX + buttonSize },
					new int[] { startY, startY + buttonSize, startY + buttonSize, startY }, 4);
			buttonMap.put(size0, Tile.building);
			buildingSizeMap.put(size0, 0);
			g.fillPolygon(size0);
			g.setColor(Color.WHITE);
			g.setFont(new Font("sansserif", Font.BOLD, 20));
			g.drawString("1", startX, startY + 20);

			g.setColor(Color.DARK_GRAY);
			startX = (int) startX + (2 * buttonSize) + 20;
			buttonSize = 80;
			startY = (int) HEIGHT - buttonSize;

			Polygon size1 = new Polygon(new int[] { startX, startX, startX + buttonSize, startX + buttonSize },
					new int[] { startY, startY + buttonSize, startY + buttonSize, startY }, 4);
			buttonMap.put(size1, Tile.building);
			buildingSizeMap.put(size1, 1);
			g.fillPolygon(size1);
			g.setColor(Color.WHITE);
			g.drawString("2", startX, startY + 20);

			g.setColor(Color.DARK_GRAY);
			startX = (int) startX + (2 * buttonSize) + 20;
			buttonSize = 80;
			startY = (int) HEIGHT - buttonSize;

			Polygon size2 = new Polygon(new int[] { startX, startX, startX + buttonSize, startX + buttonSize },
					new int[] { startY, startY + buttonSize, startY + buttonSize, startY }, 4);
			buttonMap.put(size2, Tile.building);
			buildingSizeMap.put(size2, 2);
			g.fillPolygon(size2);
			g.setColor(Color.WHITE);
			g.drawString("3", startX, startY + 20);

			g.setColor(Color.DARK_GRAY);
			startX = (int) startX + (2 * buttonSize) + 20;
			buttonSize = 80;
			startY = (int) HEIGHT - buttonSize;

			Polygon size3 = new Polygon(new int[] { startX, startX, startX + buttonSize, startX + buttonSize },
					new int[] { startY, startY + buttonSize, startY + buttonSize, startY }, 4);
			buttonMap.put(size3, Tile.building);
			buildingSizeMap.put(size3, 3);
			g.fillPolygon(size3);
			g.setColor(Color.WHITE);
			g.drawString("4", startX, startY + 20);

			g.setColor(Color.DARK_GRAY);
			startX = (int) startX + (2 * buttonSize) + 20;
			buttonSize = 80;
			startY = (int) HEIGHT - buttonSize;

			Polygon size4 = new Polygon(new int[] { startX, startX, startX + buttonSize, startX + buttonSize },
					new int[] { startY, startY + buttonSize, startY + buttonSize, startY }, 4);
			buttonMap.put(size4, Tile.building);
			buildingSizeMap.put(size4, 4);
			g.fillPolygon(size4);
			g.setColor(Color.WHITE);
			g.drawString("5", startX, startY + 20);
		} else if(tabState == 3) {
			
		}
		
		g.setColor(Color.WHITE);
		g.setFont(new Font("sansserif", Font.BOLD, 20));
		g.drawString("Tiles", 4 * (WIDTH / 32) + WIDTH / 32, HEIGHT - HEIGHT / 8 + 20);
		g.drawString("Size", 5 * (WIDTH / 32) + WIDTH / 32 + 50, HEIGHT - HEIGHT / 8 + 20);
		g.drawString("Buildings", 6 * (WIDTH / 32) + WIDTH / 32 + 100, HEIGHT - HEIGHT / 8 + 20);
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
		setupFrame(currentFrame);
		currentFrame++;
		renderLoop();
	}
}
