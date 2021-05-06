import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
                "Java", "Kotlin", "zipka.zip", "testfile.txt"
        ));

//        Filler f = new  Filler("Java", "Avaj", "zipka.zip", "testfile.txt");
//        MainApp app = new MainApp();

//        try {
//            File zip = app.getFileFromResource("zipka.zip");
//            System.out.println(zip.getAbsolutePath());
//        } catch (URISyntaxException e) {e.printStackTrace();}

//        File file = new File("resources/zipka.zip");
//        System.out.println(file.getAbsolutePath());

    }

    /**
     * Вставляет строковое значение заместо шаблона в текстовый файл, находящийся в ZIP-архиве.
     *
     * @param filler Клосс, содержащий шаблон, значение для подстановки в шаблон, путь к архиву, путь к файлу в архиве.
     */
    private static void readAndSwap(Filler filler) {

        /*
        File file = new File(filler.getPathToZip());
        Path pathForFS = Paths.get(file.getParent(), filler.getPathToZip());
         */

        try {
            MainApp app = new MainApp();
            URL resource = app.getClass().getClassLoader().getResource(filler.getPathToZip());
            File zipFile = Paths.get(resource.toURI()).toFile();
//            System.out.println(zipFile.getAbsolutePath());

            FileInputStream fileInputStream = new FileInputStream(zipFile.getAbsolutePath());
//            FileInputStream fileInputStream = new FileInputStream(filler.getPathToZip());
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
//            deleteTempFile();

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
    private static String getContents(String pathToZip, String fileNameInZip) throws IOException, URISyntaxException {

        URL resource = new MainApp().getClass().getClassLoader().getResource(pathToZip);
        File file = Paths.get(resource.toURI()).toFile();

        ZipFile zipFile = new ZipFile(file.getAbsolutePath());
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
    private static void addToArchive(Filler filler) throws IOException, URISyntaxException {

        Map<String, String> env = new HashMap<>();
        env.put("create", "true");

//        @todo: поправить абсолютный путь
//        File file = new File(filler.getPathToZip());
        File altered = new File("altered.tmp");
//        Path pathForFS = Paths.get(file.getParent(), filler.getPathToZip());


        URL resource = new MainApp().getClass().getClassLoader().getResource(filler.getPathToZip());
        File zipFile = Paths.get(resource.toURI()).toFile();
//        URI uri = URI.create("jar:file:" + zipFile.getAbsolutePath());
        URI uri = zipFile.toURI();

        try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
//            Path externalTxtFile = Paths.get("/Users/alexey/IdeaProjects/zipfile/altered.tmp");
            Path externalTxtFile = Paths.get(altered.getAbsolutePath());
            Path pathInZipfile = zipfs.getPath(filler.getFileNameInZip());
            Files.copy(externalTxtFile, pathInZipfile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

//    /**
//     * Удаляет временный файл "altered.tmp".
//     *
//     * @throws IOException
//     */
//    private static void deleteTempFile() throws IOException {
//
//        Path tempFile = Paths.get("/Users/alexey/IdeaProjects/zipfile/altered.tmp");
//        Files.delete(tempFile);
//
//    }

//    private File getFileFromResource(String fileName) throws URISyntaxException {
//
//        ClassLoader classLoader = getClass().getClassLoader();
//        URL url = classLoader.getResource(fileName);
//        if (url == null) {
//            throw new IllegalArgumentException("File not found: " + fileName);
//        } else {
//            return new File(url.toURI());
//        }
//
//    }
}
