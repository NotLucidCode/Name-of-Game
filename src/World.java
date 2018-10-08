import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public class World {
	static ArrayList<ArrayList<Tile>> world = new ArrayList<ArrayList<Tile>>();
	public static void loadWorld(String fileName) {
		FileReader FR = null;
		int character, row = 0, type = 0;
		HashMap<Integer, Tile> map = new HashMap<Integer, Tile>();
		for(Tile t : Tile.values()) {
			map.put(t.getType(), t);
		}
		try {
			FR = new FileReader(ResourceManager.load(fileName));
			world.add(new ArrayList<Tile>());
			while(true) { //reads through all characters in the file and stops on EOF (-1)
				character = FR.read();
				if (character == 32) {//if the character is a space reset the type
					if (map.get(type) != null) {
						world.get(row).add(map.get(type));
					} else {
						world.get(row).add(Tile.nil);
					}
					
					type = 0;
				} else if (character == 10) { //if the character is an enter add one to the row count
					world.add(new ArrayList<Tile>());
					world.get(row).add(map.get(type));
					type = 0;
					row++;
				} else if (character == -1) {// represents that we have reached the end of the file, it adds in the last number and then breaks out of the loop
					world.get(row).add(map.get(type));
					break;
				} else if (character == 45) {// if the character is a - indicating a negative
					type = - Integer.parseInt(String.valueOf(Character.toChars(FR.read())));
				} else if (character <= 57 && character >= 48) { //general case where the character is a regular number
					if (type >= 0) {
						type = type * 10 + Integer.parseInt(String.valueOf(Character.toChars(character))); // shifts the number one to the left then adds the new digit
					} else {
						type = type * 10 - Integer.parseInt(String.valueOf(Character.toChars(character)));
					}
				} else {
					System.out.println(character);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
