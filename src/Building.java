
public class Building {
	private int x, y, size;
	
	public Building(int x, int y, int size) {
		this.x = x;
		this.y = y;
		this.size = size;
	}
	public void create() {
		if(this.size == 0) {
			World.world.get(this.y).set(this.x, Tile.building);
			World.world.get(this.y).set(this.x + 1, Tile.buildingSlave);
		}
	}
}
