import java.awt.Point;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

public class World {
	static ArrayList<ArrayList<Tile>> world = new ArrayList<ArrayList<Tile>>();
	static FileReader FR = null;
	static int character, row = 0, type = 0;
	static volatile Hashtable<Integer, Tile> tileTable = new Hashtable<Integer, Tile>();
	static {//Static initialization block
		for(Tile t : Tile.values()) {
			tileTable.put(t.getType(), t);
		}
	}
	public static void loadWorld(String fileName) {
		try {
			
			FR = new FileReader(ResourceManager.load(fileName));
			world.add(new ArrayList<Tile>());
			while(true) { //reads through all characters in the file and stops on EOF (-1)
				character = FR.read();
				if (character == 32) {//if the character is a space reset the type
					if (tileTable.containsKey(type)) {
						world.get(row).add(tileTable.get(type));
					} else {
						world.get(row).add(Tile.nil);
					}
					type = 0;
				} else if (character == 10) { //if the character is an enter add one to the row count
					world.add(new ArrayList<Tile>());
					world.get(row).add(tileTable.get(type));
					type = 0;
					row++;
				} else if (character == -1) {// represents that we have reached the end of the file, it adds in the last number and then breaks out of the loop
					world.get(row).add(tileTable.get(type));
					break;
				} else if (character == 45) {// if the character is a - indicating a negative
					type = - Integer.parseInt(String.valueOf(Character.toChars(FR.read())));
				} else if (character <= 57 && character >= 48) { //general case where the character is a regular number
					if (type >= 0) {
						type = type * 10 + Integer.parseInt(String.valueOf(Character.toChars(character))); // shifts the number one to the left then adds the new digit
					} else {
						type = type * 10 - Integer.parseInt(String.valueOf(Character.toChars(character)));
					}
				} else if (character == 124) {// if the character is a pipe | then it is a building slave with a reference
					int x = 0, y = 0, tile;
					tile = type;
					type = 0;
					character = FR.read();
					while(character != 32) {
						if(character != 44) {
							type = type * 10 + Integer.parseInt(String.valueOf(Character.toChars(character)));
						} else {
							x = type;
							type = 0;
						}
						character = FR.read();
					}
					y = type;
					world.get(row).add(tileTable.get(tile));
					Tile.buildingSlave.getRpTable().put(new Point(world.get(row).size() - 1, row), new Point(x,y));
					//Tile.buildingSlave.getRpTable().forEach((key, value)-> System.out.println("Slave Point: " + key + "\n" + "Master Point: " + value));
					type = 0;
				} else {
					System.out.println(character);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void saveWorld() throws IOException {
		int col = 0, row = 0;
		FileWriter FW = new FileWriter(ResourceManager.load("WorldNew"));
		for (ArrayList<Tile> tiles : world) {
			String s = "";
			for (Tile t : tiles) {
				if(t.equals(Tile.buildingSlave)) {
					s += t.getType() + "|" + t.getRpTable().get(new Point(col,row)).x + "," + t.getRpTable().get(new Point(col, row)).y + " ";
				} else {
					s += t.getType() + " ";
				}
				col++;
			}
			row++;
			col = 0;
			FW.write(s + "\n");
		}
		FW.close();
	}
}
