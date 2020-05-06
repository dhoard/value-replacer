package com.github.dhoard;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class ValueReplacer {

    public static void main(String[] args) throws IOException {
        new ValueReplacer().run(args);
    }

    private void run(String[] args) throws IOException {
        File templateFile;
        File outputFile = null;
        String contents = null;
        String oldValue = null;
        String newValue = null;

        File instructionFile = new File(args[0]);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(instructionFile));

        while (true) {
            String line = bufferedReader.readLine();

            if (null == line) {
                break;
            }

            if (0 == line.trim().length()) {
                continue;
            }

            if (line.startsWith("file ")) {
                if (null != outputFile) {
                    save(outputFile, contents);
                }

                outputFile = new File(line.substring(line.indexOf("file ") + "file ".length()));
                templateFile = new File(outputFile.getAbsolutePath() + ".TEMPLATE");

                if (!templateFile.exists()) {
                    System.out.println("Creating template file ...");
                    Files.copy(outputFile.toPath(), templateFile.toPath());
                } else {
                    System.out.println("Found existing template file ...");
                }

                contents = load(templateFile);
            }

            if (line.startsWith("oldValue ")) {
                oldValue =  line.substring(line.indexOf("oldValue ") + "oldValue ".length());
            }

            if (line.startsWith("newValue ")) {
                newValue =  line.substring(line.indexOf("newValue ") + "newValue ".length());
            }

            if ((null != newValue) && (null != oldValue)) {
                if (null == contents) {
                    throw new RuntimeException("file has not been loaded!");
                }

                System.out.println("Replacing ...");
                System.out.println("  oldValue = [" + oldValue + "]");
                System.out.println("  newValue = [" + newValue + "]");

                contents = contents.replaceAll(Pattern.quote(oldValue), newValue);
                oldValue = null;
                newValue = null;
            }
        }

        if (null != contents) {
            save(outputFile, contents);
        }
    }

    private static String load(File file) throws IOException  {
        System.out.println("Loading file [" + file.getAbsolutePath() + "]");
        return new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
    }

    private static void save(File file, String contents) throws IOException {
        System.out.println("Saving file [" + file.getAbsolutePath() + "]");
        BufferedWriter bufferedWriter = null;

        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(contents);
        } finally {
            if (null != bufferedWriter) {
                bufferedWriter.close();

                try {
                    bufferedWriter.close();
                } catch (Throwable t) {
                    // DO NOTHING
                }
            }
        }
    }
}
