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
		tileSizes.add(new Polygon(new int[]{this.x - 1, this.x, this.x + 2, this.x}, new int[]{this.y, this.y, this.y, this.y + 1}, 4));
		tileSizes.add(new Polygon(new int[]{this.x - 1, this.x + 3, this.x + 6, this.x + 3}, new int[]{this.y, this.y - 1, this.y, this.y + 1}, 4));
		tileSizes.add(new Polygon(new int[]{this.x - 1, this.x + 5, this.x + 10, this.x + 5}, new int[]{this.y, this.y - 1 , this.y, this.y + 2}, 4));
		tileSizes.add(new Polygon(new int[]{this.x - 1, this.x + 7, this.x + 14, this.x + 7}, new int[]{this.y, this.y - 2 , this.y, this.y + 2}, 4));
		tileSizes.add(new Polygon(new int[]{this.x - 1, this.x + 9, this.x + 18, this.x + 9}, new int[]{this.y, this.y - 2 , this.y, this.y + 3}, 4));
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
				if(this.size % 2 == 0) {
					if(this.x % 4 == 3) {
						yList[row] = (row % 2 == 1 ? y * StateMachine.render.sideLength : y * StateMachine.render.sideLength + StateMachine.render.sideLength / 2);
					} else {
						yList[row] = (row % 2 == 1 ? y * StateMachine.render.sideLength - StateMachine.render.sideLength / 2: y * StateMachine.render.sideLength);
					}
				} else {
					if(this.x % 4 == 3) {
						yList[row] = y * StateMachine.render.sideLength + StateMachine.render.sideLength / 2;
					} else {
						yList[row] = y * StateMachine.render.sideLength;
					}
				}
				row++;
			}
			this.buildingSizeList.add(new Polygon(xList, yList, p.npoints));
		}
		for (int x = 0; x < (int) World.world.get(0).size() * 2 + 2; x++) {
			for (int y = 0; y < (int) World.world.size() + 2; y++) {
				int pixelX = (x - (int) x / 2) * StateMachine.render.height + 1;
				int pixelY = ((x - (int) x / 2) % 2 == 0 ? y * StateMachine.render.sideLength : y * StateMachine.render.sideLength - StateMachine.render.sideLength / 2) + 1;
				if(this.buildingSizeList.get(this.size).contains(pixelX, pixelY)) {
					World.world.get(y).set(x, Tile.buildingSlave);
					Tile.buildingSlave.getRpTable().put(new Point(x, y), new Point(this.x, this.y));
				}
			}
		} 
		World.world.get(this.y).set(this.x, Tile.building);
		Tile.buildingSlave.getRpTable().put(new Point(this.x + 1, this.y), new Point(this.x, this.y));
		
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
	public ArrayList<Polygon> getBuildingSizeList() {
		return this.buildingSizeList;
	}
}
