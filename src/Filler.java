public class Filler {
    private final String template;
    private final String fillingValue;
    private final String pathToZip;
    private final String fileNameInZip;

    public Filler(String template, String fillingValue, String pathToZip, String fileNameInZip) {
        this.template = template;
        this.fillingValue = fillingValue;
        this.pathToZip = pathToZip;
        this.fileNameInZip = fileNameInZip;
    }

    public String getTemplate() {
        return template;
    }

    public String getFillingValue() {
        return fillingValue;
    }

    public String getPathToZip() {
        return pathToZip;
    }

    public String getFileNameInZip() {
        return fileNameInZip;
    }
}
