package main;

import enums.Os;
import files.Files;
import helpers.Functions;
import helpers.Variables;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
    public static void main(String[] args) {
        // Test
        Scanner input = new Scanner(System.in);
        Os os = Functions.getOs();
        String command;

        // Initialize current directory
        Functions.initCurrentDirectory(os);
        AtomicReference<File> currentDirectory = new AtomicReference<>(Files.getCurrentDirectory());

        String st = " $ ";
        System.out.print(currentDirectory.get().getPath() + st);
        Process initProcess = null;
        try {
            initProcess = Functions.runCommand("cd .", currentDirectory.get(), os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // main loop
        while (!(command = input.nextLine()).equalsIgnoreCase("exit")) {
            // Run command
            AtomicReference<Process> process = new AtomicReference<>(initProcess);
            String finalCommand = command;
            new Thread(() -> {
                try {
                    process.set(Functions.runCommand(finalCommand, currentDirectory.get(), os));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();


            // print result
            Process finalProcess = process.get();
            Variables.currentLine = 0;
            new Thread(() -> {
                try {
                    String result = Functions.getResult(finalProcess, os);
                    while (true) {
                        String line = Functions.getNextLine(result);
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                currentDirectory.set(Files.getCurrentDirectory());
                System.out.print(currentDirectory.get().getPath() + st);
            }).start();

        }
    }
}
