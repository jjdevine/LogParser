package com.jonathandevinesoftware;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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
    private static List<String> filteredPhrases = new ArrayList<>();

    public static void main( String[] args ) throws Exception
    {
        if(args.length != 2 && args.length != 3) {
            quitWithMessage("Usage: \n" +
                    ">java -jar LogParser.jar <directory> <searchPhrase>\n" +
                    ">java -jar LogParser.jar <directory> <searchPhrase> <filteredPhrasesFile>");
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

        if(args.length == 3) {
            File filteredPhrasesFile = new File(args[2]);
            if (!filteredPhrasesFile.exists()) {
                quitWithMessage("<" + args[2] + "> does not exist");
            }

            try(Stream<String> lines = Files.lines(Paths.get(filteredPhrasesFile.getPath()))) {

                filteredPhrases =
                        lines
                                .filter(line -> line.trim().length() != 0)
                                .map(String::toUpperCase)
                                .collect(Collectors.toList());

            } catch (IOException ex) {
                ex.printStackTrace();
                quitWithMessage(ex.getMessage());
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
                        .filter(line -> !stringContainsAnyOf(line, filteredPhrases))
                        .forEach(line -> writeToOutputFile(out, line));

                out.flush();
                out.close();

                if(outputFile.length() == 0) {
                    outputFile.delete();
                }

            } catch (IOException ex) {
                ex.printStackTrace();
                quitWithMessage(ex.getMessage());
            }
        }
    }

    public static boolean stringContainsAnyOf(String string, List<String> values) {

        for(String value: values) {
            if (string.toUpperCase().contains(value)) {
                return true;
            }
        }

        return false;
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
