package com.jonathandevinesoftware;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Hello world!
 *
 */
public class App 
{
    private static String directory;
    private static File outputDirectory;
    private static String searchPhrase;

    public static void main( String[] args ) throws Exception
    {
        if(args.length != 2) {
            quitWithMessage("Usage: >LogParser.jar -jar <directory> <searchPhrase>");
        }

        directory = args[0];
        searchPhrase = args[1];
        outputDirectory = new File(directory + "/LogFileParser");
        if(!outputDirectory.exists()) {

            System.out.println("Creating " + outputDirectory);
            if (!outputDirectory.mkdir()) {
                quitWithMessage("could not create output directory " + outputDirectory);
            }


        }

        File logDir = new File(directory);

        if(!logDir.isDirectory()) {
            quitWithMessage("<" + directory + "> is not a directory");
        }

        for(File f : logDir.listFiles()) {

            if(f.isDirectory()) {
                continue;
            }

            System.out.println("Processing " + f.getAbsolutePath());

            try(Stream<String> lines = Files.lines(Paths.get(f.getPath()))) {

                File outputFile = new File(outputDirectory.getAbsoluteFile() + "/" + f.getName());

                if(!outputFile.exists()) {
                    outputFile.createNewFile();
                }

                BufferedWriter out = new BufferedWriter(new FileWriter(outputFile, true));

                lines
                        .filter(line -> line.toUpperCase().contains(searchPhrase.toUpperCase()))
                        .forEach(line -> writeToOutputFile(out, line));

            } catch (IOException ex) {
                ex.printStackTrace();
                quitWithMessage(ex.getMessage());
            }
        }
    }

    public static void writeToOutputFile(BufferedWriter out, String line) {

        try {
            out.append(line + "\n");
        } catch (IOException e) {
            e.printStackTrace();
            quitWithMessage(e.getMessage());
        }
    }

    public static void quitWithMessage(String message) {
        System.out.println(message);
        System.exit(0);
    }
}
