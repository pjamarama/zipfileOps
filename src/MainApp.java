import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/*
+ Прочитать архив
Найти файл с нужным именем
+ Считать его содержимое
+ Заменить содержимое
Создать файл добавить его в архив
Удалить оригинальный
Переменовать в оригинальный

 */


public class MainApp {
    public static void main(String[] args) {
        readAndSwap("zipka.zip");
    }

    private static void readAndSwap(String path) {
        try {

            ZipFile zipFile = new ZipFile(path);
            ZipEntry zipEntry = zipFile.getEntry("testfile.txt");
            InputStream inputStream = zipFile.getInputStream(zipEntry);

            String contents = new BufferedReader(new InputStreamReader(inputStream))
                    .lines().collect(Collectors.joining("\n"));

            contents = swap(contents);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String swap(String line) {
        return line.replace("Java", "Avaj");
    }
}
