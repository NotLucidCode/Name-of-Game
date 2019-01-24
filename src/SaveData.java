import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

public class SaveData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<Building> buildings = new ArrayList<Building>();
	ArrayList<ArrayList<Tile>> world = new ArrayList<ArrayList<Tile>>();
	Hashtable<Point, Point> rpTable = new Hashtable<Point, Point>();
}
