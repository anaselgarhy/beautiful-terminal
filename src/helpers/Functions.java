package helpers;

import enums.Os;
import files.Files;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

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
        if (command.contains("cd") && command.length() > 3) {
            String slash = getSlash(os);
            String currentPath = currentDirectory.getPath();
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
            setCurrentDir(currentPath.length() == 2? currentPath + slash : currentPath);
        }
        String runCommand = (os == Os.WINDOWS? "cmd /" : "sh -") + "c " + command;
        return Runtime.getRuntime().exec(runCommand, null, currentDirectory);
    }

    public static String getResult(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        boolean isPath = false;
        int i = 0;
        StringBuilder result = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            isPath = isPath(line);
            result.append(line).append('\n');
            i++;
        }
        if (isPath && i == 1) {
            setCurrentDir(result.toString());
            return "";
        }
        return result.toString();
    }
    public static boolean isPath(String line) {
        return line.contains(getSlash(getOs()));
    }

    public static void setCurrentDir(String path) {
        Files.setCurrentDirectory(new File(path.replace("\n", "")));
    }
    public static void initCurrentDirectory(Os os) {
        try {
            Process process = runCommand((os == Os.WINDOWS) ? "cd" : "ls", null, os);
            getResult(process);
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

}
