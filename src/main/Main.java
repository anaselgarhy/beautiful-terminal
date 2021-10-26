package main;

import enums.Os;
import files.Files;
import helpers.Functions;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Test
        Scanner input = new Scanner(System.in);
        Os os = Functions.getOs();
        String command;

        // Initialize current directory
        Functions.initCurrentDirectory(os);
        File currentDirectory = Files.getCurrentDirectory();

        String st = " $ ";
        System.out.print(currentDirectory.getPath() + st);
        // main loop
        while (!(command = input.nextLine()).equalsIgnoreCase("exit")) {
            // Run command
            Process process = null;
            try {
                process = Functions.runCommand(command, currentDirectory, os);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // print result
            try {
                System.out.println(Functions.getResult(process));

            } catch (IOException e) {
                e.printStackTrace();
            }
            currentDirectory = Files.getCurrentDirectory();
            System.out.print(currentDirectory.getPath() + st);
        }
    }
}
