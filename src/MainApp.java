import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MainApp {
    public static void main(String[] args) {
        readAndSwap("../zipfile/zipka.zip");
    }

    private static void readAndSwap(String path) {
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            ZipInputStream zipInputStream = new ZipInputStream(bufferedInputStream);

            ZipEntry zipEntry;
            List<String> contents = new ArrayList<>();

            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                contents.add(zipEntry.toString());
            }

            System.out.println(contents);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String swap(String line) {
        return line.replace("Java", "Avaj");
    }
}
