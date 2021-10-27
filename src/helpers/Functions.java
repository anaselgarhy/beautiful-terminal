package helpers;

import enums.Os;
import files.Files;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Functions {

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
    public static Process runCommand(String command, File currentDirectory, Os os) throws IOException {
        setDirectory(command, currentDirectory, os);
        String runCommand = (os == Os.WINDOWS? "cmd /" : "sh -") + "c " + command;
        return Runtime.getRuntime().exec(runCommand, null, currentDirectory);
    }

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
        if (isPath && i == 1) {
            setCurrentDir(result.toString());
            return "";
        }
        return result.toString();
    }
    public static boolean isPath(String line, Os os) {
        return line.contains(getSlash(os));
    }

    public static void setCurrentDir(String path) {
        Files.setCurrentDirectory(new File(path.replace("\n", "")));
    }
    public static void initCurrentDirectory(Os os) {
        try {
            Process process = runCommand((os == Os.WINDOWS) ? "cd" : "ls", null, os);
            getResult(process, os);
        } catch (IOException ignored) { }
    }

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

    public static String getSlash(Os os) {
        return (os == Os.WINDOWS? "\\" : "/");
    }

    public static String getNextLine(String result) {
        String[] tempArr = result.split("\n");
        if (Variables.currentLine < tempArr.length)
            return tempArr[Variables.currentLine++];
        return "";
    }

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
