package it.uniroma2.dicii.amod.onerjscheduling.control;

import it.uniroma2.dicii.amod.onerjscheduling.entities.Instance;
import it.uniroma2.dicii.amod.onerjscheduling.entities.OneRjProblem;
import it.uniroma2.dicii.amod.onerjscheduling.entities.output.InstanceClass;
import it.uniroma2.dicii.amod.onerjscheduling.entities.output.InstanceExecResult;
import it.uniroma2.dicii.amod.onerjscheduling.entities.output.SolverPerformance;
import it.uniroma2.dicii.amod.onerjscheduling.io.CSVExporterPrinter;
import it.uniroma2.dicii.amod.onerjscheduling.solvers.AMPLSolver;
import it.uniroma2.dicii.amod.onerjscheduling.solvers.Solver;
import it.uniroma2.dicii.amod.onerjscheduling.utils.InstanceGenerator;
import it.uniroma2.dicii.amod.onerjscheduling.utils.ProblemStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class coordinates all the operations directly involved in the executions of the solving
 * of the 1|r_j|f problem
 */
public class ExecutionManager {
    private final List<InstanceExecResult> toExport = new ArrayList<>();
    private final List<InstanceClass> classes = new ArrayList<>();
    private String name = "";
    private String outDT = "";

    /**
     * The actual solving.
     *
     * @param problem
     */
    public void solve(OneRjProblem problem) {
        this.name = problem.getName() == null ? "" : "-" + problem.getName() + "-";
        this.outDT = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        this.runSolvers(problem);
        this.buildStatistics(problem);
    }

    /**
     * For each instance, and for each solver, solve the 1|rj|f problem
     *
     * @param problem the 1|r_j|f problem to be solved
     */
    private void runSolvers(OneRjProblem problem) {
        for (Instance instance : problem.getInstances()) {
            List<SolverThread> solverThreads = new ArrayList<>();
            List<Thread> threads = new ArrayList<>();
            System.out.println("=======================================================" +
                    "\nStarting analysis for the instance " + instance.getPath());
            System.out.println("-------------------------------------------------------");

            // Optimal (commercial/AMPL) solvers and BnB "Full" solver are the ones that
            // use the majority of the execution time, often reaching timeout,
            // so run them simultaneously in a pool thread
            for (Solver solver : problem.getOptimalSolvers()) {
                System.out.println("Solving\n"
                        + "\tProblem:\t1|r_j|" + problem.getObjectFunction().getMathNotation() + "\n"
                        + "\tInstance:\t" + instance.getPath() + "\n"
                        + "\tSolver:\t\t" + solver.initName() + "\n"
                        + "It may take a while, please wait...\n");
                SolverThread foo = new SolverThread(solver, problem.getObjectFunction(), instance);
                solverThreads.add(foo);
                Thread thread = new Thread(foo);
                threads.add(thread);
                thread.start();
            }
            try {
                for (Thread thread : threads) {
                    thread.join();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (SolverThread thrClass : solverThreads) {
                InstanceExecResult result = thrClass.getResult();
                instance.addResult(result);
                System.out.println("Solution:\t\t" + instance.getResults().get(instance.getResults().size() - 1).getSolution());
                System.out.println("Time elapsed:\t" + instance.getResults().get(instance.getResults().size() - 1).getTime() + " ms");
                System.out.println("-------------------------------------------------------");
            }

            // "Relaxed" solvers are (way) less time-requiring, so they can be run sequentially
            for (Solver solver : problem.getRelaxedSolvers()) {
                System.out.println("Determining lower bound for the previous problem via preemptive relaxation\n"
                        + "\tProblem:\t1|r_j, pmnt|" + problem.getObjectFunction().getMathNotation() + "\n"
                        + "\tInstance:\t" + instance.getPath() + "\n"
                        + "\tSolver:\t\t" + solver.initName() + "\n"
                        + "It may take a while, please wait...\n");
                InstanceExecResult item = solver.solve(problem.getObjectFunction(), instance);
                item.setPath(instance.getPath());
                instance.addResult(item);
                System.out.println("Solution:\t\t" + instance.getResults().get(instance.getResults().size() - 1).getSolution());
                System.out.println("Time elapsed:\t" + instance.getResults().get(instance.getResults().size() - 1).getTime() + " ms");
                System.out.println("-------------------------------------------------------");
            }
            this.toExport.addAll(instance.getResults());
            System.out.println("Analysis finished for this instance.\nFinal solution:\t" + instance.getBestObtainedSolution()
                    + "\nisOptimal:\t\t" + instance.isOptimal());//+"=======================================================");
            // identify the instance class which the instances are referring to
            InstanceClass instClass = this.obtainClass(instance.getPath());
            instClass.addInstance(instance);
        }
        CSVExporterPrinter.getSingletonInstance().convertAndExport(this.toExport,
                "/output/report/report" + name + outDT + "-executions.csv");
    }

    /**
     * Parse a file name trying to classify it to the proper <code>InstanceClass</code>,
     * looking for matching with values from <code>InstanceGenerator.Size</code>,
     * <code>InstanceGenerator.Variance</code>,<code>InstanceGenerator.ReleaseDates</code>.
     *
     * @param path the filename to be parsed
     * @return the class best-matching the instance file classification.
     */
    private InstanceClass obtainClass(String path) {
        // si occupa anche di aggiungere la classe alla lista, qualora non esistesse
        InstanceClass ret = new InstanceClass();
        ret.setSize(fetchInstClassAttribute(InstanceGenerator.Size.values(), path));
        ret.setVariance(fetchInstClassAttribute(InstanceGenerator.Variance.values(), path));
        ret.setReleaseTimes(fetchInstClassAttribute(InstanceGenerator.ReleaseDates.values(), path));
        InstanceClass instanceClass = fetchClassIfAlreadyExistent(ret);
        return instanceClass;
    }

    /**
     * For the classification of the instances, check if the file name matches one of the classes-defining parameters
     *
     * @param enumeration set of attributes to be matched
     * @param needle      filename to be parsed
     * @return name of the class-defining attribute, if found; <code>null</code> elsewhere.
     */
    private String fetchInstClassAttribute(Object[] enumeration, String needle) {
        for (Object val : enumeration) {
            if (needle.contains(val.toString())) return val.toString();
        }
        return null;
    }

    /**
     * Find out whether an instance class was already defined in the past executions or not.
     *
     * @param instanceClass class to be checked
     * @return the already defined class, if any; or <code>instanceClass</code> elsewhere.
     */
    private InstanceClass fetchClassIfAlreadyExistent(InstanceClass instanceClass) {
        for (InstanceClass iteratedClass : this.classes) {
            //NOTICE: equals() was not used, because null values are admissible
            if (iteratedClass.getSize() == (instanceClass.getSize()) &&
                    iteratedClass.getVariance() == (instanceClass.getVariance()) &&
                    iteratedClass.getReleaseTimes() == (instanceClass.getReleaseTimes()))
                return iteratedClass;
        }
        this.classes.add(instanceClass);
        return instanceClass;
    }

    /**
     * Once solvers terminate their execution, compute average statistics over the total of the executions,
     * defining the performance of all the solvers for this problem.
     *
     * @param problem the 1|r_j|f problem to be solved
     */
    private void buildStatistics(OneRjProblem problem) {
        List<SolverPerformance> toExport = new ArrayList<>();
        for (InstanceClass instanceClass : this.classes) {
            List<Solver> solvers = new ArrayList<>();
            solvers.addAll(problem.getOptimalSolvers());
            solvers.addAll(problem.getRelaxedSolvers());
            for (Solver solver : solvers) {
                SolverPerformance solverPerf = new SolverPerformance();
                solverPerf.setSolver(solver.getSolverName());
                double avgTime = 0;
                double avgAbsErr = 0;
                double avgRelErr = 0;
                int optimalsCount = 0;
                Map<ProblemStatus, Double> statusesAvg = new HashMap();
                double nodesCtr = 0;
                for (Instance report : instanceClass.getInstances()) {
                    for (InstanceExecResult result : report.getResults()) {
                        if (result.getSolverName().equals(solver.getSolverName())) {
                            if (report.isOptimal() && result.getSolution() == report.getOptimalOrEstimate())
                                optimalsCount++;
                            avgTime += result.getTime();
                            avgAbsErr += result.getSolution() - report.getOptimalOrEstimate();
                            avgRelErr += report.getOptimalOrEstimate() == 0 ? 0 : result.getSolution() / report.getOptimalOrEstimate();
                            nodesCtr += result.getTotalVisitedNodes();
                            for (ProblemStatus status : ProblemStatus.values()) {
                                if (solver instanceof AMPLSolver) {
                                    // AMPL solvers don't use Branch-and-bound nodes, so counting them is pointless
                                    statusesAvg.put(status, -1.0);
                                } else {
                                    Double present = result.getStatuses().get(status) == null ? 0.0 : result.getStatuses().get(status);
                                    if (statusesAvg.containsKey(status)) {
                                        statusesAvg.put(status, statusesAvg.get(status) + present);
                                    } else
                                        statusesAvg.put(status, present);
                                }
                            }
                        }
                    }
                }
                avgTime /= instanceClass.getInstances().size();
                avgAbsErr /= instanceClass.getInstances().size();
                avgRelErr /= instanceClass.getInstances().size();
                solverPerf.setAvgTime(avgTime);
                solverPerf.setAvgAbsoluteError(avgAbsErr);
                solverPerf.setAvgRelativeError(avgRelErr);
                solverPerf.setOptimalsCount(optimalsCount);
                solverPerf.setOptimalsPerc(100 * optimalsCount / instanceClass.getInstances().size());
                solverPerf.setInstancesNo(instanceClass.getInstances().size());
                for (ProblemStatus status : statusesAvg.keySet()) {
                    if (!(solver instanceof AMPLSolver)) {
                        statusesAvg.put(status, statusesAvg.get(status) / instanceClass.getInstances().size());
                        nodesCtr += statusesAvg.get(status);
                    }
                }
                solverPerf.setStatusesAvg(statusesAvg);
                solverPerf.setTotalVisitedNodes(solver instanceof AMPLSolver ? -1 : nodesCtr / instanceClass.getInstances().size());
                instanceClass.addSolverPerf(solverPerf);
            }
        }
        for (InstanceClass instanceClass : this.classes) {
            for (SolverPerformance s : instanceClass.getSolversPerfs())
                toExport.add(s);
        }
        CSVExporterPrinter.getSingletonInstance().convertAndExport(this.classes,
                "/output/report/report" + this.name + this.outDT + ".csv");
        System.out.println("Done.");
    }
}
