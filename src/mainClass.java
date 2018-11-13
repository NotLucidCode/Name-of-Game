import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JFrame;
import javax.swing.WindowConstants;


public class mainClass{
	static int camX = 0;
	static int camY = 0;
	static int sideLength = 20;
	static int height = (int) (sideLength * Math.sin(Math.PI / 3));//this is the formula to find the height of a equalderal triangle thanks google!
	static int step = 10;
	
	public static void main(String[] args)  {
		
		JFrame frame = new JFrame("Render Test");
		Canvas canvas = new Canvas();
		
		frame.setVisible(true);
		frame.setSize(500, 500);
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
				int x = (int)(e.getX() + camX) * 2 / (height);
				new Building(x % 2 == 0 ? x -1 : x, (int)(e.getY() + camY) / sideLength, 0).create();				
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
		frame.addMouseWheelListener(new MouseWheelListener() {

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
        frame.addKeyListener(new KeyListener() {

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
		Graphics g = canvas.getGraphics();
		World.loadWorld("World");
		while(true) {
			//theoretically this should be put on a 60hz loop but for now this is fine
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
			System.out.print("");//actually really important: if the the for loops above stop having an output (like when were at the edge of the renderable area) java will skip them and then terminate the empty while loop ending the program, later this will not be necessary as we will fill this loop with other stuff
		}

	}
}
