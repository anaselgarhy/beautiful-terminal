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
        String runCommand = Variables.shell.getExec() + command;
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
            isPath = Functions.isPath(line);
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
        this.command = processCommandPath(command);
        return this;
    }

    public Command setProcess(Process process) {
        this.process = process;
        return this;
    }

    private String processCommandPath(String command) {
        StringBuilder commandProcessed = new StringBuilder();
        boolean dot = false, doubleQuestionOpened = false, singleQuestionOpened = false;
        if (!command.startsWith("cd ")
                && (command.contains(" .")
                || command.contains(" ." + Variables.separators[0])
                || command.contains(" ." + Variables.separators[1]))) {
            for (int i = 0; i < command.length(); i++) {
                if (command.charAt(i) == '.'
                        && ((i + 1 >= command.length() && i - 1 > 0 && command.charAt(i - 1) == ' ')
                        || (i + 1 <= command.length() && i - 1 > 0 && command.charAt(i - 1) == ' ' && command.charAt(i + 1) == ' ')
                        || ((i + 1 <= command.length() && i - 1 > 0 && command.charAt(i - 1) == ' ')
                        && (command.charAt(i + 1) == Variables.separators[0] || command.charAt(i + 1) == Variables.separators[1])))) {
                    commandProcessed.append(Functions.processPath(currentDirectory.getPath(), os));
                    dot = true;
                } else {
                    if (commandProcessed.toString().endsWith(String.valueOf(Variables.questions[0]))
                            || commandProcessed.toString().endsWith(String.valueOf(Variables.questions[1])) && dot
                            && (command.charAt(i) == Variables.separators[0] || command.charAt(i) == Variables.separators[1]))
                        commandProcessed.deleteCharAt(commandProcessed.toString().length() - 1);


                    if (!((command.charAt(i) == Variables.questions[0] || command.charAt(i) == Variables.questions[1]) && dot))
                        commandProcessed.append(command.charAt(i));
                    else if (command.charAt(i) == Variables.questions[0])
                        singleQuestionOpened = !singleQuestionOpened;
                    else if (command.charAt(i) == Variables.questions[1])
                        doubleQuestionOpened = !doubleQuestionOpened;

                    if (dot && !(singleQuestionOpened || doubleQuestionOpened) && ((command.length() <= i + 1)
                            || (i + 2 < command.length() && command.charAt(i + 1) == ' ' && command.charAt(i + 2) == '-'))) {
                        commandProcessed.append("\"");
                        dot = false;
                    }
                }
            }
        } else {
            int skip = 0;
            if (command.startsWith("cd "))
                if (command.charAt(3) == '.'
                        && (command.charAt(4) == Variables.separators[0]
                        || command.charAt(4) == Variables.separators[1]))
                        skip = 5;
            commandProcessed.append((skip > 0)? "cd " : "").append(command.substring(skip));
        }
        return commandProcessed.toString();
    }
}
