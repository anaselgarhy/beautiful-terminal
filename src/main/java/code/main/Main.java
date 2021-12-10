package code.main;

import code.gui.HelloApplication;
import code.core.enums.Os;
import code.core.enums.Shell;
import code.core.files.Directory;
import code.core.helpers.Functions;
import code.core.helpers.Variables;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        /* --------------------------------- Test --------------------------------- */
        Scanner input = new Scanner(System.in);
        Os os = Functions.getOs();
        Directory currentDirectory = new Directory();
        Directory previousDirectory = new Directory();
        Command command = new Command(currentDirectory, previousDirectory, os);

        // Initialize shell
        Variables.shell = Shell.POWERSHELL; // To test
        // Initialize slash
        Variables.separator = File.separator.charAt(0);
        // Initialize current directory
        Functions.initCurrentDirectory(os, currentDirectory);

        new HelloApplication().run();
        String st = " $ ";

        // main loop
        do {
            System.out.print(currentDirectory.getPath() + st);
            command.setCommand(input.nextLine());
            // Run command
            try {
                command.run(false);
                System.out.print(command.getResult());
            } catch (IOException e) {
                e.printStackTrace();
            }
//            currentDirectory = Directory.getDirectory();
        } while (!command.getCommand().equalsIgnoreCase("exit"));
    }
}
