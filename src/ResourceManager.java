import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ResourceManager {
	public static File load(String fileName){
		return new File("./Content/" + fileName);
	}
	public static void save(String fileName, Object obj) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("./Content/" + fileName)); 
        out.writeObject(obj);
        out.close();
	}
	public static Object loadObject(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException{
        ObjectInputStream in = new ObjectInputStream(new FileInputStream("./Content/" + fileName)); 
        Object tempObject = in.readObject();
        in.close();
        return tempObject;
	}
}
