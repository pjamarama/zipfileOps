import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

// @todo: избавиться от временного файла
public class MainApp {
    public static void main(String[] args) {
        readAndSwap(new Filler(
                "Java", "Avaj", "zipka.zip", "testfile.txt"
        ));
    }

    /**
     * Вставляет строковое значение заместо шаблона в текстовый файл, находящийся в ZIP-архиве.
     *
     * @param filler Клосс, содержащий шаблон, значение для подстановки в шаблон, путь к архиву, путь к файлу в архиве.
     */
    private static void readAndSwap(Filler filler) {

        try (FileInputStream fileInputStream = new FileInputStream(filler.getPathToZip());
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
             ZipInputStream zipInputStream = new ZipInputStream(bufferedInputStream)) {

            ZipEntry entry;
            String contents = "";

            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.getName().equals(filler.getFileNameInZip())) {
                    contents = getContents(filler.getPathToZip(), filler.getFileNameInZip());
                }
            }

            String alteredContents = contents.replace("Java", "Avaj");
            createTempFile(alteredContents);
            addToArchive(filler);
            deleteTempFile();

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
        ZipFile zipFile = new ZipFile(pathToZip);
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
    private static void addToArchive(Filler filler) throws IOException {

        Map<String, String> env = new HashMap<>();
        env.put("create", "true");

//        @todo: поправить абсолютный путь
//        URI uri = URI.create("jar:file:/Users/alexey/IdeaProjects/zipfile/zipka.zip");
        URI uri = URI.create(filler.getPathToZip());

        try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
            Path externalTxtFile = Paths.get("/Users/alexey/IdeaProjects/zipfile/altered.tmp");
            Path pathInZipfile = zipfs.getPath(filler.getFileNameInZip());
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
