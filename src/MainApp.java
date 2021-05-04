import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;


public class MainApp {
    public static void main(String[] args) {
        readAndSwap("zipka.zip");
    }

    private static void readAndSwap(String archiveFileName) {

        try (FileInputStream fileInputStream = new FileInputStream(archiveFileName);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
             ZipInputStream zipInputStream = new ZipInputStream(bufferedInputStream)) {

            ZipEntry entry;
            String contents = "";

            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.getName().equals("testfile.txt")) {
                    contents = getContents(archiveFileName);
                }
            }

            String alteredContents = contents.replace("Java", "Avaj");
            createTempFile(alteredContents);
            addToArchive();
            deleteTempFile();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Возвращает содержимое файла "testfile.txt", который хранится в zip-архиве,
     * в виде объекта String.
     *
     * @param path Имя zip-архива
     * @return Содержимое testfile.txt
     * @throws IOException
     */
    private static String getContents(String path) throws IOException {
        ZipFile zipFile = new ZipFile(path);
        ZipEntry zipEntry = zipFile.getEntry("testfile.txt");
        InputStream inputStream = zipFile.getInputStream(zipEntry);

        return new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining("\n"));
    }

    /**
     * Записывает во временный файл "./altered.tmp" изменённое содержимое "testfile.txt".
     *
     * @param alteredContents Содержимое testfile.txt после изменений
     * @throws IOException
     */
    private static void createTempFile(String alteredContents) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("altered.tmp"))) {
            bw.write(alteredContents);
        }
    }

    /**
     * Добавляет содержимое altered.tmp в архив и переименовывает его в
     * "/testfile.txt" с заменой одноименного существующего файла.
     *
     * @throws IOException
     */
    private static void addToArchive() throws IOException {

        Map<String, String> env = new HashMap<>();
        env.put("create", "true");

        URI uri = URI.create("jar:file:/Users/alexey/IdeaProjects/zipfile/zipka.zip");

        try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
            Path externalTxtFile = Paths.get("/Users/alexey/IdeaProjects/zipfile/altered.tmp");
            Path pathInZipfile = zipfs.getPath("/testfile.txt");
            Files.copy(externalTxtFile,pathInZipfile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Удаляет временный файл "altered.tmp".
     *
     * @throws IOException
     */
    private static void deleteTempFile() throws IOException {

            Path tempFile = Paths.get("/Users/alexey/IdeaProjects/zipfile/altered.tmp");
            Files.delete(tempFile);

    }
}
