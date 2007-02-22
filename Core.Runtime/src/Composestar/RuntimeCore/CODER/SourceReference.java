package Composestar.RuntimeCore.CODER;

/**
 * SourceReference
 * The SourceReference is a location in the source code.
 */
public class SourceReference {
    private String fileName;
    private int sourceLineBegin = 0;
    private int sourceLineEnd = 0;
    private int sourceColumBegin = 0;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getSourceLineBegin() {
        return sourceLineBegin;
    }

    public void setSourceLineBegin(int sourceLineBegin) {
        this.sourceLineBegin = sourceLineBegin;
    }

    public int getSourceLineEnd() {
        return sourceLineEnd;
    }

    public void setSourceLineEnd(int sourceLineEnd) {
        this.sourceLineEnd = sourceLineEnd;
    }

    public int getSourceColumBegin() {
        return sourceColumBegin;
    }

    public void setSourceColumBegin(int sourceColumBegin) {
        this.sourceColumBegin = sourceColumBegin;
    }

    public int getSourceColumEnd() {
        return sourceColumEnd;
    }

    public void setSourceColumEnd(int sourceColumEnd) {
        this.sourceColumEnd = sourceColumEnd;
    }

    private int sourceColumEnd = 0;

    public String readSource(){
        return "Source code";
    }
}
