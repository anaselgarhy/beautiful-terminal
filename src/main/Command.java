package main;

import enums.Os;
import files.Directory;
import helpers.Functions;
import helpers.Variables;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Command {
    private String command;
    private final Os os;
    private final Directory currentDirectory, previousDirectory;
    private Process process;

    public Command(Directory currentDirectory, Directory previousDirectory, Os os) {
        this.os = os;
        this.currentDirectory = currentDirectory;
        this.previousDirectory = previousDirectory;
    }

    public void run(boolean andPrint) throws IOException {
        process =  buildProcess().start();
        if (andPrint)
            System.out.print(getResult());
    }

    /**
     * It is used to build the process so that we can execute it and print the logs at the same time
     * @return Object from ProcessBuilder class that represents It is a ready-to-implement process
     */
    private ProcessBuilder buildProcess() {
        String runCommand = (os == Os.WINDOWS? "cmd /" : "sh -") + "c " + command;
        // split command
        StringTokenizer st = new StringTokenizer(runCommand);
        String[] cmdarray = new String[st.countTokens()];
        for (int i = 0; st.hasMoreTokens(); i++)
            cmdarray[i] = st.nextToken();
        return new ProcessBuilder(cmdarray)
                .directory(currentDirectory.getDirectory()).inheritIO();
    }

    /**
     * It is used to obtain the results of the execution of the command and used to set current directory
     * @return The result
     * @throws IOException If an I/O error occurs
     */
    public String getResult() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        boolean isPath = false;
        int i = 0;
        StringBuilder result = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            isPath = Functions.isPath(line, os);
            result.append(line).append('\n');
            i++;
        }
        // Set current directory
        Functions.setDirectory(command, process, currentDirectory, previousDirectory, os);
        if (isPath && i == 1 && process.exitValue() == 0 && currentDirectory.getPath().equals("")) {
            Functions.setDir(result.toString(), currentDirectory);
            return "";
        }
        return result.toString();
    }

    public String getCommand() {
        return command;
    }

    public Command setCommand(String command) {
        this.command = processCommandDir(command);
        return this;
    }

    public Command setProcess(Process process) {
        this.process = process;
        return this;
    }

    private String processCommandDir(String command) {
        StringBuilder commandProcessed = new StringBuilder();
        for (int i = 0; i < command.length(); i++) {
            if (command.charAt(i) == '.'
                    && ((i + 1 >= command.length() && i - 1 > 0 && command.charAt(i - 1) == ' ')
                    || (i + 1 <= command.length() && i - 1 > 0 && command.charAt(i - 1) == ' ' && command.charAt(i + 1) == ' ')
                    || (i + 1 <= command.length() && i - 1 > 0 && command.charAt(i - 1) == ' ' && command.charAt(i + 1) == Variables.slash.charAt(0)))) {
                commandProcessed.append(Functions.processPath(currentDirectory.getPath(), os));
            } else
                commandProcessed.append(command.charAt(i));
        }
        return commandProcessed.toString();
    }
}
