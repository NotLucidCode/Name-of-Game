import java.io.File;

public class ResourceManager {
	public static File load(String fileName){
		return new File("./Content/" + fileName);
	}
}
