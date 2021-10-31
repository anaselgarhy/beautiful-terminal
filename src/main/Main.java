package main;

import enums.Os;
import files.Files;
import helpers.Functions;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
    public static void main(String[] args) {
        /* --------------------------------- Test --------------------------------- */
        Scanner input = new Scanner(System.in);
        Os os = Functions.getOs();
        String command;

        // Initialize current directory
        Functions.initCurrentDirectory(os);
        File currentDirectory = Files.getCurrentDirectory();

        String st = " $ ";

        // main loop
        do {

            System.out.print(currentDirectory.getPath() + st);
            command = input.nextLine();
            // Run command
            ProcessBuilder processBuilder = Functions.buildProcess(command, currentDirectory, os);
            Process process = null;
            try {
                process = processBuilder.start();
                System.out.print(Functions.getResult(command, process, currentDirectory, os));
            } catch (IOException e) {
                e.printStackTrace();
            }
            currentDirectory = Files.getCurrentDirectory();
        } while (!command.equalsIgnoreCase("exit"));
    }
}
