import java.awt.Point;
import java.awt.Polygon;
import java.io.Serializable;
import java.util.ArrayList;

public class Building implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int x, y, size;
	private ArrayList<Polygon> buildingSizeList = new ArrayList<Polygon>();
	
	public Building(int x, int y, int size) {
		this.x = x;
		this.y = y;
		this.size = size;
		StateMachine.bsd.buildings.add(this);
	}
	public void create() {
		ArrayList<Polygon> tileSizes = new ArrayList<Polygon>();
		tileSizes.add(new Polygon(new int[]{this.x, this.x + 2}, new int[]{this.y, this.y}, 2));
		for(Polygon p : tileSizes) {
			int[] xList = {0,0,0,0};
			int[] yList = {0,0,0,0};
			int col = 0;
			for(int x : p.xpoints){
				xList[col] = (x - (int) x / 2) * StateMachine.render.height;
				col++;
			}
			int row = 0;
			for(int y : p.ypoints){
				yList[row] = (this.x - (int) this.x / 2) % 2 == 0 ? y * StateMachine.render.sideLength : y * StateMachine.render.sideLength - StateMachine.render.sideLength / 2;
				row++;
			}
			this.buildingSizeList.add(new Polygon(xList, yList, p.npoints));
		}
		if(this.size == 0) {
//			
//			int col = 0, row = 0;
//			for (ArrayList<Tile> tiles : World.world) {
//				for (Tile t : tiles) { 
//					int pixelX = (col - (int) col / 2) * StateMachine.render.height;
//					int pixelY = (col - (int) col / 2) % 2 == 0 ? row * StateMachine.render.sideLength : row * StateMachine.render.sideLength - StateMachine.render.sideLength / 2;
//					col++;
//					if(this.buildingSizeList.get(0).contains(pixelX, pixelY)) {
//						World.world.get(row).set(col, Tile.buildingSlave);
//						Tile.buildingSlave.getRpTable().put(new Point(col, row), new Point(this.x, this.y));
//						System.out.println("hi");
//					}
//				}
//				row++;
//				col = 0;
//			} 
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
	public void click(Point p) {
		boolean show = true;
		for (BuildingMenu bM : StateMachine.render.buildingMenuList) {
			if(bM.getB().equals(this)) {
				show = false;
				break;
			}
		}
		if(show) {
			StateMachine.render.buildingMenuList.add(new BuildingMenu(1920 / 2, 1080/2, this));
		}
	}
}
