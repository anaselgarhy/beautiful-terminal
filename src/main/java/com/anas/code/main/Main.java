package com.anas.code.main;

import com.anas.code.core.CustomShellCore;
import com.anas.code.core.NativeShellCore;
import com.anas.code.core.ShellCore;
import com.anas.code.core.enums.Os;
import com.anas.code.core.enums.Shell;
import com.anas.code.core.helpers.Functions;
import com.anas.code.core.helpers.Variables;
import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        /* --------------------------------- Test --------------------------------- */
        Scanner input = new Scanner(System.in);
        Os os = Functions.getOs();

        // Initialize slash
        Variables.separator = File.separator.charAt(0);

        Variables.os = os;

        ShellCore shell1 = new NativeShellCore(Shell.POWERSHELL);

        // new MainFrame().run();

        if (shell1 instanceof CustomShellCore) {
            String st = " $ ";

            // main loop
            do {
                System.out.print(shell1.getCurrentDirectory().getPath() + st);
                // Run command
                shell1.runCommand(input.nextLine());
            } while (!shell1.getCommand().toString().equalsIgnoreCase("exit"));
        }
    }
}
