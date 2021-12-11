package code.core;

import code.core.enums.Os;
import code.core.enums.Shell;
import code.core.files.Directory;
import code.core.helpers.Functions;

import java.io.File;
import java.io.IOException;

import static code.core.helpers.Variables.os;

public abstract class ShellCore {
    private final Shell shell;
    private final Command command;
    private final Directory currentDirectory, previousDirectory;

    public ShellCore(Shell shell) {
        this.shell = shell;
        this.currentDirectory = new Directory();
        this.previousDirectory = new Directory();
        initCurrentDirectory();
        command = new Command(currentDirectory, previousDirectory, shell);
    }

    public abstract void runCommand(String command);

    public Shell getShell() {
        return shell;
    }

    public Command getCommand() {
        return command;
    }

    public Directory getCurrentDirectory() {
        return currentDirectory;
    }

    public Directory getPreviousDirectory() {
        return previousDirectory;
    }

    /**
     * This function uses an initial path, usually used at the start of the program (needs to be modified)
     */
    public void initCurrentDirectory() {
        Shell sh = (os == Os.WINDOWS)? Shell.CMD : Shell.SH;
        String command = sh.getExec() + ((os == Os.WINDOWS) ? "cd" : "pwd");
        try {
            Process process = Runtime.getRuntime().exec(command, null, null);
            String path = new Command(currentDirectory, null, sh)
                    .setCommand(command)
                    .setProcess(process)
                    .getResult();
            currentDirectory.setDirectory(new File(path.replace("\n", "")));
        } catch (IOException ignored) {
        }
    }
}
