import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class MainApp {
    public static void main(String[] args) {
        readAndSwap("../zipfile/testfile.txt");
    }

    private static void readAndSwap(String path) {
        try {
            String value = "Java";
            List<String> contents = Files.readAllLines(Paths.get(path));
            for (String line : contents) {
                line = swap(line);
                System.out.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String swap(String line) {
        return line.replace("Java", "Avaj");
    }
}
