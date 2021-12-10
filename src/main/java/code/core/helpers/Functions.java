package code.core.helpers;

import code.core.enums.Os;
import code.core.files.Directory;
import code.main.Command;

import java.io.File;
import java.io.IOException;

public class Functions {

    /**
     * Get the operating system name
     *
     * @return object fom Os enum represent operating system
     */
    public static Os getOs() {
        String osName = System.getProperty("os.name").toLowerCase();
        Os os;
        if (osName.contains("win"))
            os = Os.WINDOWS;
        else if (osName.contains("lin"))
            os = Os.LINUX;
        else
            os = Os.MAC;
        return os;
    }

    public static String getHomeDirPath() {
        return System.getProperty("user.home");
    }

    /**
     * Run the command as a block, That is, it executes the entire command and then returns an object
     * from the Process class that represents all outputs
     *
     * @param command          The command
     * @param currentDirectory The directory you want to run the command in
     * @param os               The operating system
     * @return object from the Process class that represents all outputs
     * @throws IOException If he can't execute the command because he doesn't have permissions
     */
    public static Process runCommand(String command, File currentDirectory, Os os) throws IOException {
        String runCommand = (os == Os.WINDOWS ? "cmd /" : "sh -") + "c " + command;
        return Runtime.getRuntime().exec(runCommand, null, currentDirectory);
    }

    /**
     * This function is used to check whether the text represents a path or not (needs to be modified)
     *
     * @param line The line you want to check
     * @return True if the line represents a path, false if it is not a path
     */
    public static boolean isPath(String line) {
        return line.contains(String.valueOf(Variables.separator));
    }

    /**
     * This function is used to set the current path
     *
     * @param path The path
     */
    public static void setDir(String path, Directory directory) {
        directory.setDirectory(new File(path.replace("\n", "")));
    }

    /**
     * This function uses an initial path, usually used at the start of the program (needs to be modified)
     *
     * @param os The operating system
     */
    public static void initCurrentDirectory(Os os, Directory currentDirectory) {
        String command = (os == Os.WINDOWS) ? "cd" : "pwd";
        try {
            Process process = runCommand(command, null, os);
            new Command(currentDirectory, null, os)
                    .setCommand(command)
                    .setProcess(process)
                    .getResult();
        } catch (IOException ignored) {
        }
    }

    /**
     * This function is used to find the number of steps back
     *
     * @param command The change directory command
     * @return The number of steps back
     */
    public static int getNumOfBack(String command) {
        int numOfBack = 0;
        command = command.replace("cd ", "");
        for (int i = 0; i < command.length() && i + 1 < command.length(); i++) {
            String temp = command.charAt(i) + "" + command.charAt(++i);
            if (temp.equals(".."))
                numOfBack++;
        }
        return numOfBack;
    }

    /**
     * Get the result line by line
     *
     * @param result The result
     * @return the next line
     */
    public static String getNextLine(String result) {
        String[] tempArr = result.split("\n");
        if (Variables.currentLine < tempArr.length)
            return tempArr[Variables.currentLine++];
        return "";
    }

    /**
     * Set the current directory
     *
     * @param command          The command
     * @param process          The process
     * @param currentDirectory The current directory
     * @param os               The operating system
     */
    public static boolean setDirectory(String command, Process process, Directory currentDirectory, Directory previousDirectory, Os os) {
        if (currentDirectory != null) {
            try {
                process.waitFor(); // pause this thread until end process
            } catch (InterruptedException ignored) {
            }
            String currentPath = currentDirectory.getPath();
            if (command.contains("cd") && command.length() > 3 && process.exitValue() == 0) {
                String afterCd = command.substring(3);
                if (afterCd.startsWith(".."))
                    currentPath = backDir(command, currentPath, currentDirectory);
                else if (!(afterCd.startsWith(".") && !(afterCd.length() > 1))) {
                    // Clean path
                    afterCd = cleanPath(afterCd);
                    // linux short cuts
                    currentPath = shortCuts(afterCd, currentPath, previousDirectory, os);
                }
            }
            String finalPath = currentPath.length() == 2 ? currentPath + Variables.separator : currentPath;
            // Set prev dir
            if (!finalPath.equals(currentDirectory.getPath()))
                setDir(currentDirectory.getPath(), previousDirectory);
            // Test path
            if (new File(finalPath).isDirectory()) {
                setDir(finalPath, currentDirectory);
                return true;
            }
        }
        return false;
    }

    private static String backDir(String command, String currentPath, Directory currentDirectory) {
        String afterCd = command.substring(3);
        int backs = getNumOfBack(command);
        for (int i = 0; i < backs; i++) {
            String fileName = Variables.separator + currentDirectory.getName();
            int end = currentPath.length() - fileName.length();
            if (end > 0)
                currentPath = currentPath.substring(0, end);
            else {
                currentPath = currentDirectory.getPath();
                break;
            }
        }
        int temp = backs * 2;
        temp += temp - 1; // Slashes
        if (temp < afterCd.length())
            currentPath += Variables.separator + afterCd.substring(temp);
        return currentPath;
    }

    private static String cleanPath(String path) {
        String removeStart = "";
        if (path.startsWith("./") || path.startsWith(".\\"))
            removeStart = path.substring(0, 1);
        else if (path.startsWith("."))
            removeStart = ".";

        // remove . or ./
        path = path.replace(removeStart, "");

        // if user write cd "directory name" or cd 'directory name'
        if ((path.startsWith("\"") && path.endsWith("\"")) || (path.startsWith("'") && path.endsWith("'"))) {
            // remove quotation
            path = path.substring(1, path.length() - 1);
        }
        return path;
    }

    private static String shortCuts(String afterCd, String currentPath, Directory previousDirectory, Os os) {
        // linux short cuts
        if (os != Os.WINDOWS) {
            currentPath = switch (afterCd) {
                case "~" -> getHomeDirPath();
                case "/" -> "/";
                case "-" -> previousDirectory.getPath();
                default -> currentPath;
            };
        }
        if (afterCd.length() > 1)
            currentPath += Variables.separator + afterCd;

        return currentPath;
    }

    public static String processPath(String path, Os os) {
        StringBuilder processedPath = new StringBuilder();
        boolean quteAddedInStart = false;
        for (char ch : path.toCharArray()) {
            if (ch == ' ') {
                if (os != Os.WINDOWS)
                    ch = Variables.separator;
                else {
                    if (!quteAddedInStart) {
                        processedPath.insert(0, '\"');
                        quteAddedInStart = true;
                    }
                }
            }
            processedPath.append(ch);
        }
        if (quteAddedInStart)
            processedPath.append('\"');
        return processedPath.toString();
    }

    public static int getNumberOfCh(String str, char ch) {
        int numberOfCh = 0;
        for (char che : str.toCharArray())
            if (che == ch)
                numberOfCh++;
        return numberOfCh;
    }
}
