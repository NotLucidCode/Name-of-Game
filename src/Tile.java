import java.awt.Color;

public enum Tile {
	nil(-1, Color.MAGENTA),
	water(0, Color.BLUE),
	land(1, Color.GREEN),
	building(2, Color.LIGHT_GRAY),
	buildingSlave(3, Color.WHITE);//new Color(0,0,0,0));
	
	private int type;
	private Color color;
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
}
