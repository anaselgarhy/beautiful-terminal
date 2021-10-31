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
        setDirectory(command, currentDirectory, os);
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
        // Set current directory
        setDirectory(command, currentDirectory, os);
        return new ProcessBuilder(cmdarray)
                .directory(currentDirectory).inheritIO();
    }

    /**
     * It is used to obtain the results of the execution of the command
     * @param process The operation for which you want to print the results of its execution
     * @param os The operating system
     * @return The result
     * @throws IOException If an I/O error occurs
     */
    public static String getResult(Process process, Os os) throws IOException {
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
        if (isPath && i == 1) {
            setCurrentDir(result.toString());
            return "";
        }
        result.append('\b');
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
        try {
            Process process = runCommand((os == Os.WINDOWS) ? "cd" : "pwd", null, os);
            getResult(process, os);
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
     * @param currentDirectory The current directory
     * @param os The operating system
     */
    public static void setDirectory(String command, File currentDirectory, Os os) {
        if (currentDirectory != null) {
            String currentPath = currentDirectory.getPath();
            String slash = getSlash(os);
            if (command.contains("cd") && command.length() > 3) {
                String afterCd = command.substring(3);
                if (afterCd.startsWith("..")) {
                    int backs = getNumOfBack(command);

                    for (int i = 0; i < backs; i++) {
                        String fileName = slash + currentDirectory.getName();
                        int end = currentPath.length() - fileName.length();
                        currentPath = currentPath.substring(0, end);
                    }
                } else {
                    if (!(afterCd.startsWith(".") && !(afterCd.length() > 1))) {
                        currentPath += slash + afterCd.replace(".", "");
                    }
                }
            }
            String finalPath = currentPath.length() == 2 ? currentPath + slash : currentPath;
            // Test path
            if (new File(finalPath).isDirectory())
                setCurrentDir(finalPath);
        }
    }
}
