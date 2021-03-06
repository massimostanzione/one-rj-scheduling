package it.uniroma2.dicii.amod.onerjscheduling.utils;

import it.uniroma2.dicii.amod.onerjscheduling.entities.Job;
import it.uniroma2.dicii.amod.onerjscheduling.io.CSVExporterPrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static it.uniroma2.dicii.amod.onerjscheduling.utils.InstanceGenerator.Size.SIZE_MEDIUM;
import static it.uniroma2.dicii.amod.onerjscheduling.utils.InstanceGenerator.Variance.VARIANCE_LOW;

/**
 * A tool that generates a set of random job instances, based on the parameters described by the inner enums.
 */
public class InstanceGenerator {
    private final static int INSTANCE_NO = 20;

    public static void main(String[] args) {
        generate("/data/instances/generated/");
    }

    public static void generate(String path) {
        int start = 0, end = 0;//, pj = -1;
        System.out.println("Generating job instances.\nIt may take a while, please wait...");
        Random prngRjAvg = new Random();
        Random prngPjAvg = new Random();
        Random prngRj = new Random();
        Random prngPj = new Random();
        Random prngN = new Random();
        for (Size s : Size.values()) {
            switch (s) {
                case SIZE_SMALL:
                    start = 5;
                    end = 10;
                    break;
                case SIZE_MEDIUM:
                    start = 15;
                    end = 25;
                    break;
                case SIZE_LARGE:
                    start = 30;
                    end = 50;
                    break;
            }
            for (Variance v : Variance.values()) {
                int var = v == VARIANCE_LOW ? 20 : 150;
                for (ReleaseDates relDates : ReleaseDates.values()) {
                    // specific for the r_j study
                    if ((s != SIZE_MEDIUM && (relDates != ReleaseDates.RJ_RAND))
                            ||
                            (s == SIZE_MEDIUM && relDates != ReleaseDates.RJ_RAND && v == VARIANCE_LOW)) continue;

                    for (int i = 1; i <= INSTANCE_NO; i++) {
                        List<Job> jobList = new ArrayList<>();
                        int nJobs = prngN.nextInt(start, end + 1);
                        int pjAvg = prngPjAvg.nextInt(5, 300 + 1);
                        for (int j = 1; j <= nJobs; j++) {
                            int pj;
                            do {
                                pj = prngPj.nextInt(pjAvg - var, pjAvg + var);
                            } while (pj <= 0);
                            int rj = 0;
                            switch (relDates) {
                                case RJ_RAND:
                                    rj = prngRj.nextInt(0, 100 + 1);
                                    break;
                                case RJ_SMALL:
                                    rj = prngRj.nextInt(0, 5 + 1);
                                    break;
                                case RJ_PROPORTIONAL:
                                    rj = (int) (pj * .8);
                                    break;
                            }
                            Job job = new Job(j, rj, pj);
                            jobList.add(job);
                        }
                        CSVExporterPrinter.getSingletonInstance().convertAndExport(jobList, path + s + "#" + v + "#" + relDates + "#" + i + ".csv");
                    }
                }
            }
        }
        System.out.println("Done.");
    }

    public enum Size {SIZE_SMALL, SIZE_MEDIUM, SIZE_LARGE}

    public enum Variance {VARIANCE_LOW, VARIANCE_HIGH}

    public enum ReleaseDates {RJ_SMALL, RJ_RAND, RJ_PROPORTIONAL}

}
