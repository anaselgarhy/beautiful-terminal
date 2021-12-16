package code.core;

import code.core.enums.Shell;

import java.io.IOException;

public class CustomShellCore extends ShellCore{
    public CustomShellCore(Shell shell) {
        super(shell);
    }
    @Override
    public void runCommand(String command) {
            getCommand().setCommand(command);
        try {
            if (!command.isBlank()) {
                getCommand().run(true);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
