package com.github.dhoard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class ValueReplacerGenerator {

    private static final String LINE_SEPARATOR = System.lineSeparator();

    public static void main(String[] args) throws IOException {
        new ValueReplacerGenerator().run(args);
    }

    private void run(String[] args) throws IOException {
        System.out.println("file " + args[0]);

        File inputFile1 = new File(args[1]);
        File inputFile2 = new File(args[2]);

        String fileContents1 = load(inputFile1);
        String fileContents2 = load(inputFile2);

        BufferedReader bufferedReader1 = null;
        BufferedReader bufferedReader2 = null;

        int count = 0;
        StringBuilder stringBuilder = new StringBuilder();

        count++;
        bufferedReader1 = new BufferedReader(new StringReader(fileContents1));
        bufferedReader2 = new BufferedReader(new StringReader(fileContents2));

        while (true) {
            String line1 = bufferedReader1.readLine();
            String line2 = bufferedReader2.readLine();

            if (null == line1) {
                break;
            }

            if (!line1.equalsIgnoreCase(line2)) {
                stringBuilder.append(LINE_SEPARATOR);
                stringBuilder.append("oldValue " + line1);
                stringBuilder.append(LINE_SEPARATOR);
                stringBuilder.append("newValue " + line2);
            }

            count++;
        }

        System.out.println(stringBuilder.toString().trim());
    }

    private String load(File file) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(file));

        StringBuilder result = new StringBuilder();

        int index = -1;
        List<String> lineList = load0(file);

        Iterator<String> iterator = lineList.iterator();
        while (iterator.hasNext()) {
            String line = iterator.next();
            index++;

            String line2 = line.trim();

            if (0 == line2.length()) {
                // DO NOTHING
            } else if (line2.startsWith("#")) {
                // DO NOTHING
            } else {
                if (line.endsWith("\\") && !line.endsWith("\\\\")) {
                    String value = line.substring(0, line.length() - 1);

                    while (iterator.hasNext()) {
                        line = iterator.next();
                        line = line.trim();

                        // line is either a key=value or a multi-line value
                        if (line.endsWith("\\") && !line.endsWith("\\\\")) {
                            line = line.substring(0, line.length() - 1);
                            value = value + line;
                        } else {
                            value = value + line;
                            break;
                        }
                    }

                    line = value.trim();
                }
            }

            result.append(line);
            if (index > 0) {
                result.append("\r\n");
            }
        }

        return result.toString().trim();
    }

    private List<String> load0(File file) throws IOException {
        List<String> result = new ArrayList<>();
        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(new FileReader(file));

            while (true) {
                String line = bufferedReader.readLine();

                if (null == line) {
                    break;
                }

                result.add(line);
            }
        }
        finally {
            if (null != bufferedReader) {
                try {
                    bufferedReader.close();
                } catch (Throwable t) {
                    // DO NOTHING
                }
            }
        }

        return result;
    }
}
