package main;

import enums.Os;
import files.Directory;
import helpers.Functions;
import helpers.Variables;

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

        // Initialize slash
        Variables.separator = File.separator;
        // Initialize current directory
        Functions.initCurrentDirectory(os, currentDirectory);


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
