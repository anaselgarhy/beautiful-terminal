package files;

import java.io.File;

public class Directory {
    private File currentDirectory;

    public Directory() {
        currentDirectory = new File("");
    }

    public File getDirectory() {
        return currentDirectory;
    }

    public void setDirectory(File currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    public String getPath() {
        return currentDirectory.getPath();
    }

    public String getName() {
        return currentDirectory.getName();
    }
}
