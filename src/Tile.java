import java.awt.Color;
import java.awt.Point;
import java.util.Hashtable;

public enum Tile {
	nil(-2, Color.MAGENTA),
	edge(-1, Color.BLACK),
	water(0, Color.BLUE),
	land(1, Color.GREEN),
	sand(4, new Color(194,178,128)),
	building(2, Color.LIGHT_GRAY),
	buildingSlave(3, Color.WHITE);//new Color(0,0,0,0));
	
	private int type;
	private Color color;
	private Hashtable<Point, Point> rpTable = new Hashtable<Point, Point>(); //RefrencePointTable <slave position, master position>
	private Tile(int tyleType, Color tileColor){
		this.type = tyleType;
		this.color = tileColor;
	}
	public int getType() {
		return this.type;
	}
	public Color getColor() {
		return this.color;
	}
	public Hashtable<Point,Point> getRpTable() {
		return rpTable;
	}
	public void setRpTable(Hashtable<Point,Point> table) {
		rpTable = table;
	}
}
