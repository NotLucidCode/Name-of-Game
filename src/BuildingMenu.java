import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class BuildingMenu {
	private int buttonSize = 15, sizeX = 150, sizeY = 300;
	private int x, y;
	private Building b;
	public BuildingMenu(int x, int y, Building b) {
		this.x = x;
		this.y = y;
		this.b = b;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public Building getB() {
		return b;
	}
	public void setB(Building b) {
		this.b = b;
	}
	public void draw(Graphics g) {
		g.setColor(Color.GRAY);
		g.fillRect(this.x, this.y, 150, 300);
		g.setColor(Color.RED);
		g.fillRect(this.x, this.y, buttonSize, buttonSize);
		g.setColor(Color.BLACK);
		g.drawString("X: " + this.b.getX(), this.x + 10, y + 50);
		g.drawString("Y: " + this.b.getY(), this.x + 10, y + 100);
		g.drawString("Size: " + this.b.getSize(), this.x + 10, y + 150);
		
	}
	public void click(Point p) {
		if(p.x - this.x < buttonSize && p.y - this.y < buttonSize) {
			StateMachine.render.buildingMenuList.remove(this);
		}
	}
	public void drag(Point p) {
		this.x = p.x;
		this.y = p.y;
	}
	public int getButtonSize() {
		return buttonSize;
	}
	public int getSizeX() {
		return sizeX;
	}
	public int getSizeY() {
		return sizeY;
	}
}
