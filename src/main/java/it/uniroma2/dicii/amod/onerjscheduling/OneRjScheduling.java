package it.uniroma2.dicii.amod.onerjscheduling;

import it.uniroma2.dicii.amod.onerjscheduling.entities.DataInstance;
import it.uniroma2.dicii.amod.onerjscheduling.entities.ExecutionReportItem;
import it.uniroma2.dicii.amod.onerjscheduling.entities.OneRjProblem;
import it.uniroma2.dicii.amod.onerjscheduling.io.CSVExporterPrinter;
import it.uniroma2.dicii.amod.onerjscheduling.objectfunctions.ObjFunctionFactory;
import it.uniroma2.dicii.amod.onerjscheduling.objectfunctions.ObjectFunctionEnum;
import it.uniroma2.dicii.amod.onerjscheduling.solvers.*;
import it.uniroma2.dicii.amod.onerjscheduling.utils.ExternalConfig;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static it.uniroma2.dicii.amod.onerjscheduling.objectfunctions.ObjectFunctionEnum.SUM_COMPLETION_TIMES;
import static it.uniroma2.dicii.amod.onerjscheduling.utils.Consts.DEFAULT_AMPL_PATH;

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
    public static void main(String[] args) throws Exception {
        if(Objects.equals(ExternalConfig.getSingletonInstance().getAmplPath(), DEFAULT_AMPL_PATH)){
            System.out.println("Please initialize the constant AMPL_PATH in config.ini");
            System.exit(1);
        }

        System.out.println("Timeout set to " + ExternalConfig.getSingletonInstance().getComputationTimeout() + " ms.");
        System.out.println("Initializing problems and solvers...");

        OneRjProblem problem = new OneRjProblem();

        // 1. Problem name (not mandatory)
        problem.setName("rootLBmanaged");

        // 2. Object function
        problem.setObjectFunction(new ObjFunctionFactory().createObjFunction(SUM_COMPLETION_TIMES));

        // 3. Data instances
        problem.addInstance(new DataInstance("./data/lect22.csv"));
        problem.addInstance(new DataInstance("./data/lect22-reversed.csv"));
        problem.addInstance(new DataInstance("./data/10identical.csv"));
 //     problem.addInstance(new DataInstance("./data/mortonPentico-ljb12-reduced.csv"));
      problem.addInstance(new DataInstance("./data/mortonPentico-MINIMAL_LLB_TRIAL.csv"));
  //      problem.addInstance(new DataInstance("./data/mortonPentico-ljb12-smallPj.csv"));
        /*  problem.addInstance(new DataInstance("./data/mortonPentico-ljb12.csv"));*/
//problem.loadInstanceDirectory("./data/instances/generated/");
//problem.loadInstanceDirectory("./data");
    //    problem.addInstance(
     //           new DataInstance("./data/instances/generated/SIZE_SMALL_VARIANCE_SMALL_16.csv"));
        // 4. Solvers
       problem.addOptimumSolver(new AMPLGurobiSolver());
        problem.addOptimumSolver(new AMPLCplexSolver());
    //    problem.addRelaxedSolver(new BnBFullSolver());
        problem.addRelaxedSolver(new BnBFIFOSolver());
    //    problem.addRelaxedSolver(new BnBForwardSolver());
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
        List<ExecutionReportItem> toExport=new ArrayList<>();
        System.out.println("-------------------------------------------------------");
        for(DataInstance instance:problem.getInstances()){
        //for (String path : instance.getPath()) {
            for (Solver solver : problem.getOptimumSolvers()) {
                //solver.setPath(instance.getPath());
                //solver.setObjFunction(problem.getObjectFunction());
                System.out.println("Solving\n"
                        + "\tProblem:\t1|r_j|" + problem.getObjectFunction().getMathNotation() + "\n"
                        + "\tInstance:\t" + instance.getPath() + "\n"
                        + "\tSolver:\t\t" + solver.initName() + "\n"
                        + "It may take a while, please wait...\n");
                //instance.getResults().add(solver.solve());
                ExecutionReportItem item=solver.solve(problem.getObjectFunction(),instance);
                instance.addResult(item);

                System.out.println("Solution:\t\t" + instance.getResults().get(instance.getResults().size() - 1).getSolution());
                System.out.println("Time elapsed:\t" + instance.getResults().get(instance.getResults().size() - 1).getTime() + " ms");
                System.out.println("-------------------------------------------------------");
            }
            for (Solver solver : problem.getRelaxedSolvers()) {
                //solver.setPath(instance.getPath());
                //solver.setObjFunction(problem.getObjectFunction());
                System.out.println("Determining lower bound for the previous problem via preemptive relaxation\n"
                        + "\tProblem:\t1|r_j, pmnt|" + problem.getObjectFunction().getMathNotation() + "\n"
                        + "\tInstance:\t" + instance.getPath() + "\n"
                        + "\tSolver:\t\t" + solver.initName() + "\n"
                        + "It may take a while, please wait...\n");
                ExecutionReportItem item=solver.solve(problem.getObjectFunction(),instance);
                instance.addResult(item);
                System.out.println("Solution:\t\t" + instance.getResults().get(instance.getResults().size() - 1).getSolution());
                System.out.println("Time elapsed:\t" + instance.getResults().get(instance.getResults().size() - 1).getTime() + " ms");
                System.out.println("-------------------------------------------------------");
            }
            toExport.addAll(instance.getResults());
            System.out.println("\n#########\n"+instance+"\n#########\n");
        }
        String name = problem.getName() == null ? "" : "-" + problem.getName() + "-";
        CSVExporterPrinter.getSingletonInstance().convertAndExport(toExport, "/output/report" + name + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")) + ".csv");
        System.out.println("Done.");
    }
}


