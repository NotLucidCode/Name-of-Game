import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class Render implements Runnable{
	boolean isInit = false;
	int camX = 0;
	int camY = 0;
	int sideLength = 20;
	int height = (int) (sideLength * Math.sin(Math.PI / 3));//this is the formula to find the height of a equilateral triangle thanks google!
	int step = 10;
	JFrame frame = new JFrame("Render Test");
	Canvas canvas = new Canvas();
	BufferStrategy bs = null;
	boolean running = true;
	int WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width,
			HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
	GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
	Graphics g = null;
	public void renderWorld(){

		g = bs.getDrawGraphics();
		for(int x = 0; x < (int) World.world.get(0).size() * 2 + 2; x++) {
			for (int y = 0; y < (int) World.world.size() + 2; y++) {
				if(y < World.world.size() && x < World.world.get(y).size() ) {
					g.setColor(World.world.get(y).get(x).getColor());
				} else {
					g.setColor(Color.BLACK);
				}
				int pixelX = (x - (int) x/2) * height + camX;
				int pixelY = (x - (int) x/2) % 2 == 0 ? y * sideLength + camY : y * sideLength + camY - sideLength/2 ;
				g.fillPolygon(new int[]{pixelX, pixelX, x % 2 == 0 ? pixelX + height : pixelX - height},new int[]{pixelY, pixelY + sideLength, pixelY + sideLength/2}, 3 );
				
			}
		}
		g.setColor(Color.RED);
		g.fillRect(300, 300, 300, 300);
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
				System.out.println("Click at " + (e.getX() + camX) * 2 / (height) + ", " + (e.getY() + camY)/ sideLength);
				int x = (int)(e.getX() - camX) * 2 / (height);
				new Building(x % 2 == 0 ? x - 1 : x, (int)(e.getY() - camY) / sideLength, 0).create();				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
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
		canvas.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.getWheelRotation() > 0 && height > 1) {
					sideLength--;
					height = (int) (sideLength * Math.sin(Math.PI / 3));
				} else if(e.getWheelRotation() < 0) {
					sideLength++;
					height = (int) (sideLength * Math.sin(Math.PI / 3));
				}
				
			}
			
		});
        canvas.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {}

            @Override
        	public void keyPressed(KeyEvent e) {
        		if (e.getKeyCode() == 87) {//W
        			camY -= step;
        		} else if (e.getKeyCode() == 83) {//S
        			camY += step;
        		}else if (e.getKeyCode() == 65) {//A
        			camX -= step;
        		}else if (e.getKeyCode() == 68) {//D
        			camX += step;
        		}
        		
        	}
        });
		canvas.createBufferStrategy(3);
		canvas.requestFocus();
		bs = canvas.getBufferStrategy();
		isInit = true;
		
	}
	@Override
	public void run() {
		setupFrame();
	}
}


