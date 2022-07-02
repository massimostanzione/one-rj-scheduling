package it.uniroma2.dicii.amod.onerjscheduling.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Simple utility class used to convert .txt instances from TODO
 */
public class MortonPenticoInstanceConv {

    public static void main(String[] args) throws IOException {
        MortonPenticoInstanceConv inst = new MortonPenticoInstanceConv();
        inst.go(args);
    }

    public void go(String[] args) throws IOException {
        FileReader input = new FileReader(args[0]);
        BufferedReader bufRead = new BufferedReader(input);
        String myLine = null;
        int i = 0, pj;
        List<String[]> dataLines = new ArrayList<>();
        dataLines.add(new String[]{"JOB_ID", "RELEASE_DATE", "PROCESSING_TIME"});
        while ((myLine = bufRead.readLine()) != null) {
            pj = 0;
            String[] lineArr = myLine.split(" ");
            // salta la prima riga (intestazione)
            if (lineArr.length > 3) {
                i++;
                // compute the total processing time
                for (int index = 5; index <= 8 + Integer.parseInt(lineArr[3])-4; index += 2) {
                    pj += Integer.parseInt(lineArr[index]);
                }
                dataLines.add(new String[]
                        {String.valueOf(i), lineArr[0], String.valueOf(pj)});
            }
        }
        File csvOutputFile = new File(args[0].replace(".txt", ".csv"));
        this.endConversion(csvOutputFile, dataLines);

    }

    private void endConversion(File csvOutputFile, List<String[]> dataLines) {
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            dataLines.stream()
                    .map(this::convertToCSV)
                    .forEach(pw::println);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String convertToCSV(String[] data) {
        return Stream.of(data)
                // .map(this::convertToCSV)
                .collect(Collectors.joining(","));
    }
}
