package it.uniroma2.dicii.amod.onerjscheduling;

import it.uniroma2.dicii.amod.onerjscheduling.control.ExecutionManager;
import it.uniroma2.dicii.amod.onerjscheduling.entities.Instance;
import it.uniroma2.dicii.amod.onerjscheduling.entities.OneRjProblem;
import it.uniroma2.dicii.amod.onerjscheduling.objectfunctions.ObjFunctionFactory;
import it.uniroma2.dicii.amod.onerjscheduling.objectfunctions.ObjectFunctionEnum;
import it.uniroma2.dicii.amod.onerjscheduling.solvers.*;
import it.uniroma2.dicii.amod.onerjscheduling.utils.ExternalConfig;

import java.time.Duration;
import java.time.Instant;
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
        if (Objects.equals(ExternalConfig.getSingletonInstance().getAmplPath(), DEFAULT_AMPL_PATH)) {
            System.out.println("Please initialize the constant AMPL_PATH in config.ini");
            System.exit(1);
        }

        System.out.println("Timeout set to " + ExternalConfig.getSingletonInstance().getComputationTimeout() + " ms.");
        System.out.println("Initializing problems and solvers...");

        OneRjProblem problem = new OneRjProblem();

        // 1. Problem name (not mandatory)
        problem.setName("final-2min");

        // 2. Object function
        problem.setObjectFunction(new ObjFunctionFactory().createObjFunction(SUM_COMPLETION_TIMES));

        // 3. Data instances
     /*     problem.addInstance(new Instance("./data/instances/test/lect22.csv"));
        problem.addInstance(new Instance("./data/instances/test/lect22-reversed.csv"));
     //  problem.addInstance(new Instance("./data/instances/test/10identical.csv"));
       problem.addInstance(new Instance("./data/instances/test/mortonPentico-ljb12-reduced.csv"));
        problem.addInstance(new Instance("./data/instances/test/mortonPentico-MINIMAL_LLB_TRIAL.csv"));
        problem.addInstance(new Instance("./data/instances/test/mortonPentico-ljb12-smallPj.csv"));
        problem.addInstance(new Instance("./data/instances/test/mortonPentico-ljb12.csv"));*/
        problem.loadInstanceDirectory("./data/instances/generated/");
//problem.loadInstanceDirectory("./data");
        //    problem.addInstance(
        //           new DataInstance("./data/instances/generated/SIZE_SMALL_VARIANCE_SMALL_16.csv"));
        // 4. Solvers
        problem.addOptimumSolver(new AMPLGurobiSolver());
       problem.addOptimumSolver(new AMPLCplexSolver());
            problem.addRelaxedSolver(new BnBFullSolver());
         problem.addRelaxedSolver(new BnBFIFOSolver());
           problem.addRelaxedSolver(new BnBForwardSolver());
         problem.addRelaxedSolver(new BnBLLBSolver());

        System.out.println("Done. Starting solving.");
        ExecutionManager execMgr=new ExecutionManager();
        Instant start= Instant.now();
        execMgr.solve(problem);
        System.out.println("=======================================================");
        Instant end=Instant.now();
        System.out.println("\nExecution finished in "
        + Duration.between(start, end).toSeconds()+" s ("+Duration.between(start,end).toMinutes()+" min).\nDone.");
    }


}


