import java.io.File;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResourceManager {
	public static File load(String fileName){
		return new File("./Content/" + fileName);
	}
}
