import java.awt.Point;

public class Building {
	private int x, y, size;
	
	public Building(int x, int y, int size) {
		this.x = x;
		this.y = y;
		this.size = size;
		StateMachine.bsd.buildings.add(this);
	}
	public void create() {
		if(this.size == 0) {
			World.world.get(this.y).set(this.x, Tile.building);
			World.world.get(this.y).set(this.x + 1, Tile.buildingSlave);
			Tile.buildingSlave.getRpTable().put(new Point(this.x + 1, this.y), new Point(this.x, this.y));
		} else if(this.size == 1) {
			System.out.println(this.x + " " + this.y);
			World.world.get(this.y).set(this.x, Tile.building);
			World.world.get(this.y).set(this.x + 1, Tile.buildingSlave);
			Tile.buildingSlave.getRpTable().put(new Point(this.x + 1, this.y), new Point(this.x, this.y));
			World.world.get(this.y).set(this.x + 2, Tile.buildingSlave);
			Tile.buildingSlave.getRpTable().put(new Point(this.x + 2, this.y), new Point(this.x, this.y));
			World.world.get(this.y).set(this.x + 3, Tile.buildingSlave);
			Tile.buildingSlave.getRpTable().put(new Point(this.x + 3, this.y), new Point(this.x, this.y));
			World.world.get(this.y).set(this.x + 4, Tile.buildingSlave);
			Tile.buildingSlave.getRpTable().put(new Point(this.x + 4, this.y), new Point(this.x, this.y));
			World.world.get(this.y).set(this.x + 5, Tile.buildingSlave);
			Tile.buildingSlave.getRpTable().put(new Point(this.x + 5, this.y), new Point(this.x, this.y));
			World.world.get(this.y - 1).set(this.x + 2, Tile.buildingSlave);
			Tile.buildingSlave.getRpTable().put(new Point(this.x + 2, this.y - 1), new Point(this.x, this.y));
			World.world.get(this.y - 1).set(this.x + 3, Tile.buildingSlave);
			Tile.buildingSlave.getRpTable().put(new Point(this.x + 3, this.y - 1), new Point(this.x, this.y));
		}
		
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
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
}
