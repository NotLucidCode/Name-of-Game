import java.awt.Color;

public enum Tile {
	nil(-1, Color.MAGENTA),
	water(0, Color.BLUE),
	land(1, Color.GREEN);
	
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
