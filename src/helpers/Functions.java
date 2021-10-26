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
        if (command.contains(os == Os.WINDOWS? "cd" : "ls") && command.length() > 3) {
            if (command.substring(3).startsWith("..")) {

            }
            setCurrentDir(command.substring(3));
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
        String slash = (getOs() == Os.WINDOWS? "\\" : "/");
        return line.contains(slash);
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
        for (int i = 0; i < command.length(); i++) {
            String temp = "" + command.charAt(i) + command.charAt(++i);
            if (temp.equals(".."))
                numOfBack++;
        }
        return numOfBack;
    }

}
