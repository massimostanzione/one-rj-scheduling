package it.uniroma2.dicii.amod.onerjscheduling.utils;

import it.uniroma2.dicii.amod.onerjscheduling.entities.Job;
import it.uniroma2.dicii.amod.onerjscheduling.io.CSVExporterPrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static it.uniroma2.dicii.amod.onerjscheduling.utils.InstanceGenerator.Variance.VARIANCE_SMALL;

public class InstanceGenerator {
    private final static int AVG = 20;
    private final static double VARIANCE_SMALL_VAL = 0;
    private final static int VARIANCE_HIGH_VAL = 15;

    public static void main(String[] args) {
        generate("/data/instances/generated/");
    }

    public static void generate(String path) {
        int start = 0, end = 0;//, pj = -1;

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
                double var = v == VARIANCE_SMALL ? VARIANCE_SMALL_VAL : VARIANCE_HIGH_VAL;
                for (int i = 1; i <= 20; i++)// genera 20 istanze (TODO parametrizzare)
                {
                    List<Job> jobList = new ArrayList<>();
                    int nJobs = prngN.nextInt(start, end + 1);
                    System.out.println("\n" + s + " " + v);
                    for (int j = 1; j <= nJobs; j++) {
                        // genera righe per ogni istanza
                        int rjAvg = Math.max(prngRjAvg.nextInt(0, 15), 1);
                        int pjAvg = Math.max(prngPjAvg.nextInt(5, 30), 1);
                        int rj = Math.max((int) prngRj.nextGaussian(rjAvg, var), 1);
                        int pj = Math.max((int) prngPj.nextGaussian(pjAvg, var), 1);
                        System.out.println(j + "\t" + rj + "\t" + pj);
                        Job job = new Job(j, rj, pj);
                        jobList.add(job);
                    }
                    CSVExporterPrinter.getSingletonInstance().convertAndExport(jobList, path+s+"_"+v+"_"+i+".csv");
                }


            }
        }
    }


    public enum Size {SIZE_SMALL, SIZE_MEDIUM, SIZE_LARGE}

    public enum Variance {VARIANCE_SMALL, VARIANCE_LARGE}

    public enum ReleaseTimes {RJ_SMALL, RJ_RAND, RJ_PROPORTIONAL}
}
