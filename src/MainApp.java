import sun.misc.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class MainApp {
    public static void main(String[] args) {
        readAndSwap("zipka.zip");
    }

    private static void readAndSwap(String path) {
        try {

            ZipFile zipFile = new ZipFile(path);
            ZipEntry zipEntry = zipFile.getEntry("testfile.txt");
            InputStream inputStream = zipFile.getInputStream(zipEntry);


//            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
////                contents.add(zipEntry.toString());
//            }

            String contents = new BufferedReader(new InputStreamReader(inputStream))
                    .lines().peek(System.out::println).collect(Collectors.joining("\n"));

            contents = swap(contents);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String swap(String line) {
        return line.replace("Java", "Avaj");
    }
}
