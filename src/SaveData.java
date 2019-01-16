import java.io.Serializable;
import java.util.ArrayList;

public class SaveData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<Building> buildings = new ArrayList<Building>();
	ArrayList<ArrayList<Tile>> world = new ArrayList<ArrayList<Tile>>();
}
