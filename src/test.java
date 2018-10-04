import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;


public class test{
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
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		frame.add(canvas);
        frame.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {}

            @Override
        	public void keyPressed(KeyEvent e) {
        		if (e.getKeyCode() == 87) {//W
        			camY += step;
        		} else if (e.getKeyCode() == 83) {//S
        			camY -= step;
        		}else if (e.getKeyCode() == 65) {//A
        			camX += step;
        		}else if (e.getKeyCode() == 68) {//D
        			camX -= step;
        		}
        		
        	}
        });
		Graphics g = canvas.getGraphics();
		while(true) {
			//theoretically this should be put on a 60hz loop but for now this is fine
			for(int x = 0 + camX; Math.abs(x) < frame.getWidth();x += height) {
				for(int y = (x- camX)/height%2==0 ? 0 + camY : 0-sideLength/2 + camY; Math.abs(y) < frame.getHeight();y += sideLength) {
					//System.out.println(Math.abs(x) + " " + camX + " " + frame.getWidth());
					g.setColor(Color.WHITE);
					g.fillPolygon(new int[]{x, x, height + x},new int[]{y, y + sideLength, y + sideLength/2},3);
					g.setColor(Color.BLACK);
					g.fillPolygon(new int[]{x, x, x - height},new int[]{y, y + sideLength, y + sideLength/2},3);
				}
			}
			System.out.println("");//actually really important if the the for loops above stop having an output (like when were at the edge of the renderable area) java will skip them and then terminate the empty while loop ending the program, later this will not be necessary as we will fill this loop with other stuff
		}

	}
}
