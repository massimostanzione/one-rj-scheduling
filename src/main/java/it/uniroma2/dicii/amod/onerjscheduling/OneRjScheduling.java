package it.uniroma2.dicii.amod.onerjscheduling;

import it.uniroma2.dicii.amod.onerjscheduling.entities.OneRjProblem;
import it.uniroma2.dicii.amod.onerjscheduling.io.CSVExporterPrinter;
import it.uniroma2.dicii.amod.onerjscheduling.objectfunctions.ObjectFunctionEnum;
import it.uniroma2.dicii.amod.onerjscheduling.solvers.*;
import it.uniroma2.dicii.amod.onerjscheduling.utils.ExternalConfig;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Main access point for the program.
 */
public class OneRjScheduling {
    /**
     * Main method.
     * <p>
     * Please set here, as further specified, an <code>OneRjProblem</code> object the following items:
     * 1. a name (not mandatory);
     * 2. an object function (from <code>ObjectFunctionEnum</code>)
     * 3. some instance, in a CSV format;
     * 4. one or more solvers
     *
     * @param args main parameters
     * @see ObjectFunctionEnum
     */
    public static void main(String[] args) throws IOException {
        System.out.println("Timeout set to " + ExternalConfig.getSingletonInstance().getComputationTimeout() + " ms.");
        System.out.println("Initializing problems and solvers...");

        OneRjProblem problem = new OneRjProblem();

        // 1. Problem name (not mandatory)
        problem.setName("demo");

        // 2. Object function
        problem.setObjectFunction(ObjectFunctionEnum.SUM_COMPLETION_TIMES);

        // 3. Data instances
        problem.addInstance("./data/lect22.csv");
      //     problem.addInstance("./data/lect22-reversed.csv");
      //     problem.addInstance("./data/mortonPentico-ljb12-reduced.csv");
      //     problem.addInstance("./data/mortonPentico-ljb12-smallPj.csv");
      //     problem.addInstance("./data/mortonPentico-ljb12.csv");

        // 4. Solvers
        problem.addOptimumSolver(new AMPLGurobiSolver());
        problem.addOptimumSolver(new AMPLCplexSolver());
        problem.addRelaxedSolver(new BnBFullSolver());
        problem.addRelaxedSolver(new BnBFIFOSolver());
        problem.addRelaxedSolver(new BnBForwardSolver());
        problem.addRelaxedSolver(new BnBLLBSolver());

        System.out.println("Done. Starting solving.");
        solve(problem);
    }

    /**
     * The actual solving.
     *
     * @param problem
     */
    private static void solve(OneRjProblem problem) {
        System.out.println("-------------------------------------------------------");
        for (String path : problem.getInstances()) {
            for (Solver solver : problem.getOptimumSolvers()) {
                solver.setPath(path);
                solver.setObjFunction(problem.getObjectFunction());
                System.out.println("Solving\n"
                        + "\tProblem:\t1|r_j|" + solver.getObjFunction().getMathNotation() + "\n"
                        + "\tInstance:\t" + solver.getPath() + "\n"
                        + "\tSolver:\t\t" + solver.getName() + "\n"
                        + "It may take a while, please wait...\n");
                problem.getResults().add(solver.solve());
                System.out.println("Solution:\t\t" + problem.getResults().get(problem.getResults().size() - 1).getSolution());
                System.out.println("Time elapsed:\t" + problem.getResults().get(problem.getResults().size() - 1).getTime() + " ms");
                System.out.println("-------------------------------------------------------");
            }
            for (Solver solver : problem.getRelaxedSolvers()) {
                solver.setPath(path);
                solver.setObjFunction(problem.getObjectFunction());
                System.out.println("Determining lower bound for the previous problem via preemtive relaxation\n"
                        + "\tProblem:\t1|r_j, pmnt|" + solver.getObjFunction().getMathNotation() + "\n"
                        + "\tInstance:\t" + solver.getPath() + "\n"
                        + "\tSolver:\t\t" + solver.getName() + "\n"
                        + "It may take a while, please wait...\n");
                problem.getResults().add(solver.solve());
                System.out.println("Solution:\t\t" + problem.getResults().get(problem.getResults().size() - 1).getSolution());
                System.out.println("Time elapsed:\t" + problem.getResults().get(problem.getResults().size() - 1).getTime() + " ms");
                System.out.println("-------------------------------------------------------");
            }
        }
        String name = problem.getName() == null ? "" : "-" + problem.getName();
        CSVExporterPrinter.getSingletonInstance().convertAndExport(problem.getResults(), "/output/report" + name + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")) + ".csv");
        System.out.println("Done.");
    }
}


