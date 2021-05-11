import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

// @todo: избавиться от временного файла
public class MainApp {
    public static void main(String[] args) {

        readAndSwap(new Filler(
                "Java", "Kotlin", "zibberish.zip", "testfile.txt"
        ));

    }

    /**
     * Вставляет строковое значение заместо шаблона в текстовый файл, находящийся в ZIP-архиве.
     *
     * @param filler Клосс, содержащий шаблон, значение для подстановки в шаблон, путь к архиву, путь к файлу в архиве.
     */
    private static void readAndSwap(Filler filler) {

        try {
            FileInputStream fileInputStream = new FileInputStream(getAbsPath(filler.getPathToZip()));
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            ZipInputStream zipInputStream = new ZipInputStream(bufferedInputStream);

            ZipEntry entry;
            String contents = "";

            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.getName().equals(filler.getFileNameInZip())) {
                    contents = getContents(filler.getPathToZip(), filler.getFileNameInZip());
                }
            }

            String alteredContents = contents.replace(filler.getWordBefore(), filler.getWordAfter());
            createTempFile(alteredContents);
            addToArchive(filler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Возвращает содержимое файла, который хранится в zip-архиве,
     * в виде объекта String.
     *
     * @param pathToZip Имя zip-архива
     * @return Содержимое файла в архиве
     * @throws IOException
     */
    private static String getContents(String pathToZip, String fileNameInZip) throws IOException {

        ZipFile zipFile = new ZipFile(getAbsPath(pathToZip));
        ZipEntry zipEntry = zipFile.getEntry(fileNameInZip);
        InputStream inputStream = zipFile.getInputStream(zipEntry);

        return new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining("\n"));
    }

    /**
     * Записывает во временный файл "./altered.tmp" изменённое содержимое файла из архива.
     *
     * @param alteredContents Содержимое файла из архива после изменений
     * @throws IOException
     */
    private static void createTempFile(String alteredContents) throws IOException {
        File tempFile = File.createTempFile("altered", null);
        tempFile.deleteOnExit();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {
            bw.write(alteredContents);
        }
    }

    /**
     * Добавляет содержимое altered.tmp в архив и переименовывает его
     * с заменой одноименного существующего файла.
     *
     * @throws IOException
     */
    private static void addToArchive(Filler filler) throws IOException {
        File altered = new File("altered.tmp");
        Path absPathToZip = Paths.get(getAbsPath(filler.getPathToZip()));

        try (FileSystem zipfs = FileSystems.newFileSystem(absPathToZip, null)) {
            Path externalTxtFile = Paths.get(altered.getAbsolutePath());
            Path pathInZipfile = zipfs.getPath(filler.getFileNameInZip());
            Files.copy(externalTxtFile, pathInZipfile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Возвращает строковый объект, содержащий абсолютный путь файла, имя которого передаётся в параметах
     *
     * @param fileName Имя файла
     * @return Абсолютный путь файла
     */
    private static String getAbsPath(String fileName) {

        ClassLoader classLoader = MainApp.class.getClassLoader();
        URL s = classLoader.getResource(fileName);
        File file = new File(classLoader.getResource(fileName).getFile());
        System.out.println(file.getAbsolutePath());
        return file.getAbsolutePath();

    }
}
