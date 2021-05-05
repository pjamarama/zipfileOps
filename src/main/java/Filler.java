public class Filler {
    private final String wordBefore;
    private final String wordAfter;
    private final String pathToZip;
    private final String fileNameInZip;

    public Filler(String wordBefore, String wordAfter, String pathToZip, String fileNameInZip) {
        this.wordBefore = wordBefore;
        this.wordAfter = wordAfter;
        this.pathToZip = pathToZip;
        this.fileNameInZip = fileNameInZip;
    }

    public String getWordBefore() {
        return wordBefore;
    }

    public String getWordAfter() {
        return wordAfter;
    }

    public String getPathToZip() {
        return pathToZip;
    }

    public String getFileNameInZip() {
        return fileNameInZip;
    }
}
