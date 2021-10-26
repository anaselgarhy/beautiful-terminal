package files;

import java.io.File;

public class Files {
    private static File currentDirectory;
    public static void setCurrentDirectory(File currentDirectory) {
        Files.currentDirectory = currentDirectory;
    }
    public static File getCurrentDirectory() {
        return currentDirectory;
    }
}
