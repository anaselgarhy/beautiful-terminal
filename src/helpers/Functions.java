package helpers;

import enums.Os;
import files.Files;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Functions {

    /**
     * Get the operating system name
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
     * @param command The command
     * @param currentDirectory The directory you want to run the command in
     * @param os The operating system
     * @return object from the Process class that represents all outputs
     * @throws IOException If he can't execute the command because he doesn't have permissions
     */
    public static Process runCommand(String command, File currentDirectory, Os os) throws IOException {
        String runCommand = (os == Os.WINDOWS? "cmd /" : "sh -") + "c " + command;
        return Runtime.getRuntime().exec(runCommand, null, currentDirectory);
    }

    /**
     * It is used to build the process so that we can execute it and print the logs at the same time
     * @param command The command
     * @param currentDirectory The directory you want to run the command in
     * @param os The operating system
     * @return Object from ProcessBuilder class that represents It is a ready-to-implement process
     */
    public static ProcessBuilder buildProcess(String command, File currentDirectory, Os os) {
        String runCommand = (os == Os.WINDOWS? "cmd /" : "sh -") + "c " + command;
        // split command
        StringTokenizer st = new StringTokenizer(runCommand);
        String[] cmdarray = new String[st.countTokens()];
        for (int i = 0; st.hasMoreTokens(); i++)
            cmdarray[i] = st.nextToken();
        return new ProcessBuilder(cmdarray)
                .directory(currentDirectory).inheritIO();
    }

    /**
     * It is used to obtain the results of the execution of the command
     * @param command The command
     * @param process The operation for which you want to print the results of its execution
     * @param currentDirectory The current directory
     * @param os The operating system
     * @return The result
     * @throws IOException If an I/O error occurs
     */
    public static String getResult(String command, Process process, File currentDirectory, Os os) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        boolean isPath = false;
        int i = 0;
        StringBuilder result = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            isPath = isPath(line, os);
            result.append(line).append('\n');
            i++;
        }
        // Set current directory
        setDirectory(command, process, currentDirectory, os);
        if (isPath && i == 1 && process.exitValue() == 0) {
            setCurrentDir(result.toString());
            return "";
        }
        return result.toString();
    }

    /**
     * This function is used to check whether the text represents a path or not (needs to be modified)
     * @param line The line you want to check
     * @param os The operating system
     * @return True if the line represents a path, false if it is not a path
     */
    public static boolean isPath(String line, Os os) {
        return line.contains(getSlash(os));
    }

    /**
     * This function is used to set the current path
     * @param path The path
     */
    public static void setCurrentDir(String path) {
        Files.setCurrentDirectory(new File(path.replace("\n", "")));
    }

    /**
     * This function uses an initial path, usually used at the start of the program (needs to be modified)
     * @param os The operating system
     */
    public static void initCurrentDirectory(Os os) {
        String command = (os == Os.WINDOWS) ? "cd" : "pwd";
        try {
            Process process = runCommand(command, null, os);
            getResult(command , process, null, os);
        } catch (IOException ignored) { }
    }

    /**
     * This function is used to find the number of steps back
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
     * This function is used to get the system slash
     * @param os The operating system
     * @return The slash use in this operating system
     */
    public static String getSlash(Os os) {
        return (os == Os.WINDOWS? "\\" : "/");
    }

    /**
     * Get the result line by line
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
     * @param command The command
     * @param process The process
     * @param currentDirectory The current directory
     * @param os The operating system
     */
    public static boolean setDirectory(String command, Process process, File currentDirectory, Os os) {
        if (currentDirectory != null) {
            try {
                process.waitFor(); // pause this thread until end process
            } catch (InterruptedException ignored) {}
            String currentPath = currentDirectory.getPath();
            if (command.contains("cd") && command.length() > 3 && process.exitValue() == 0) {
                String afterCd = command.substring(3);
                if (afterCd.startsWith("..")) {
                    int backs = getNumOfBack(command);
                    for (int i = 0; i < backs; i++) {
                        String fileName = Variables.slash + currentDirectory.getName();
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
                        currentPath += Variables.slash + afterCd.substring(temp);
                } else {
                    if (!(afterCd.startsWith(".") && !(afterCd.length() > 1))) {
                        String removeStart = "";
                        if (afterCd.startsWith("./") || afterCd.startsWith(".\\"))
                            removeStart = afterCd.substring(0, 1);
                        else if (afterCd.startsWith("."))
                            removeStart = ".";

                        // remove . or ./
                        afterCd = afterCd.replace(removeStart, "");

                        // if user write cd "directory name" or cd 'directory name'
                        if ((afterCd.startsWith("\"") && afterCd.endsWith("\"")) || (afterCd.startsWith("'") && afterCd.endsWith("'"))) {
                            // remove quotation
                            afterCd = afterCd.substring(1, afterCd.length() - 1);
                        }
                        // linux short cuts
                        if (os != Os.WINDOWS) {
                            if (afterCd.equals("~"))
                                currentPath = getHomeDirPath();
                            else if (afterCd.equals("/"))
                                currentPath = "/";
                        } else // windows
                            currentPath += Variables.slash + afterCd;
                    }
                }
            }
            String finalPath = currentPath.length() == 2 ? currentPath + Variables.slash : currentPath;
            // Test path
            if (new File(finalPath).isDirectory()) {
                setCurrentDir(finalPath);
                return true;
            }
        }
        return false;
    }
}
