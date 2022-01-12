package code.main;

import code.core.CustomShellCore;
import code.core.NativeShellCore;
import code.core.ShellCore;
import code.core.enums.Os;
import code.core.enums.Shell;
import code.core.helpers.Functions;
import code.core.helpers.Variables;
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
