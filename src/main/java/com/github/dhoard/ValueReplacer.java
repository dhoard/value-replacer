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

    public static final String INFO = "INFO";

    public static void main(String[] args) throws IOException {
        new ValueReplacer().run(args);
    }

    private void run(String[] args) throws IOException {
        File templateFile;
        File outputFile = null;
        String contents = null;
        String oldValue = null;
        String newValue = null;
        String appendValue = null;

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
                    log(INFO, "Creating template file [" + templateFile.getAbsolutePath() + "]");
                    Files.copy(outputFile.toPath(), templateFile.toPath());
                } else {
                    log(INFO,"Found existing template file [" + templateFile.getAbsolutePath() + "]");
                }

                contents = load(templateFile);
            }

            if (line.startsWith("oldValue ")) {
                oldValue =  line.substring(line.indexOf("oldValue ") + "oldValue ".length());
            }

            if (line.startsWith("newValue ")) {
                newValue =  line.substring(line.indexOf("newValue ") + "newValue ".length());
            }

            if (line.startsWith("appendValue ")) {
                appendValue =  line.substring(line.indexOf("appendValue ") + "appendValue ".length());

                if (null == contents) {
                    throw new RuntimeException("file has not been loaded!");
                }

                log(INFO, "A [" + appendValue + "]");

                contents = contents + System.getProperty("line.separator");
                contents = contents + appendValue;

                oldValue = null;
                newValue = null;
            }

            if ((null != newValue) && (null != oldValue)) {
                if (null == contents) {
                    throw new RuntimeException("file has not been loaded!");
                }

                log(INFO, "O [" + oldValue + "]");
                log(INFO, "N [" + newValue + "]");

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
        log(INFO,"Loading file [" + file.getAbsolutePath() + "]");
        return new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
    }

    private static void save(File file, String contents) throws IOException {
        log(INFO,"Saving file [" + file.getAbsolutePath() + "]");
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

    private static void log(String label, String message) {
        System.out.println("[" + label + "] " + message);
    }
}
