package com.anas.code.core;

import com.anas.code.core.enums.Shell;

import java.io.IOException;

public class NativeShellCore extends ShellCore {
    public NativeShellCore(Shell shell) {
        super(shell);
        runCommand(shell.getName());
    }

    @Override
    public void runCommand(String command) {
        getCommand().setCommand(command);
        try {
            getCommand().run(true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
