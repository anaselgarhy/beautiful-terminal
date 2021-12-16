package code.core.files;

import java.io.File;

public class Directory {
    private File file;

    public Directory() {
        file = new File("");
    }

    public File getDirectory() {
        return file;
    }

    public void setDirectory(File file) {
        this.file = file;
    }

    public String getPath() {
        return file.getPath();
    }
    public void setPath(String path) {
       this.file = new File(path);
    }

    public String getName() {
        return file.getName();
    }
}
