package main;

import enums.Os;
import files.Directory;
import helpers.Functions;
import helpers.Variables;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        /* --------------------------------- Test --------------------------------- */
        Scanner input = new Scanner(System.in);
        Os os = Functions.getOs();
        String command;
        Directory currentDirectory = new Directory();
        Directory previousDirectory = new Directory();

        // Initialize slash
        Variables.slash = Functions.getSlash(os);
        // Initialize current directory
        Functions.initCurrentDirectory(os, currentDirectory);


        String st = " $ ";

        // main loop
        do {

            System.out.print(currentDirectory.getPath() + st);
            command = input.nextLine();
            // Run command
            ProcessBuilder processBuilder = Functions.buildProcess(command, currentDirectory.getDirectory(), os);
            Process process = null;
            try {
                process = processBuilder.start();
                System.out.print(Functions.getResult(command, process, currentDirectory, previousDirectory, os));
            } catch (IOException e) {
                e.printStackTrace();
            }
//            currentDirectory = Directory.getDirectory();
        } while (!command.equalsIgnoreCase("exit"));
    }
}
