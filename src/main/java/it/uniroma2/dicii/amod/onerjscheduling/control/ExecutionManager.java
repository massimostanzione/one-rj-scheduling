package it.uniroma2.dicii.amod.onerjscheduling.control;

import it.uniroma2.dicii.amod.onerjscheduling.entities.Instance;
import it.uniroma2.dicii.amod.onerjscheduling.entities.OneRjProblem;
import it.uniroma2.dicii.amod.onerjscheduling.entities.output.InstanceClass;
import it.uniroma2.dicii.amod.onerjscheduling.entities.output.InstanceExecResult;
import it.uniroma2.dicii.amod.onerjscheduling.entities.output.SolverPerformance;
import it.uniroma2.dicii.amod.onerjscheduling.io.CSVExporterPrinter;
import it.uniroma2.dicii.amod.onerjscheduling.solvers.Solver;
import it.uniroma2.dicii.amod.onerjscheduling.utils.InstanceGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExecutionManager {
    private final List<InstanceExecResult> toExport = new ArrayList<>();
    private final List<InstanceClass> classes = new ArrayList<>();

    /**
     * The actual solving.
     *
     * @param problem
     */
    public void solve(OneRjProblem problem) {
        System.out.println("-------------------------------------------------------");
        for (Instance instance : problem.getInstances()) {
            for (Solver solver : problem.getOptimumSolvers()) {
                //solver.setPath(instance.getPath());
                //solver.setObjFunction(problem.getObjectFunction());
                System.out.println("Solving\n"
                        + "\tProblem:\t1|r_j|" + problem.getObjectFunction().getMathNotation() + "\n"
                        + "\tInstance:\t" + instance.getPath() + "\n"
                        + "\tSolver:\t\t" + solver.initName() + "\n"
                        + "It may take a while, please wait...\n");
                //instance.getResults().add(solver.solve());
                InstanceExecResult item = solver.solve(problem.getObjectFunction(), instance);
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
                InstanceExecResult item = solver.solve(problem.getObjectFunction(), instance);
                instance.addResult(item);
                System.out.println("Solution:\t\t" + instance.getResults().get(instance.getResults().size() - 1).getSolution());
                System.out.println("Time elapsed:\t" + instance.getResults().get(instance.getResults().size() - 1).getTime() + " ms");
                System.out.println("-------------------------------------------------------");
            }
            this.toExport.addAll(instance.getResults());
            System.out.println("\n#########\n" + instance + "\n#########\n");


            //l'instExecResult deve essere aggiunto alla classe di appartenenza

            // quindi anzitutto decifra a quale classe appartiene
            // la funzione la aggiunge pure alla lista, qualora non ci fosse
            InstanceClass instClass = this.obtainClass(instance.getPath());
            instClass.addInstance(instance);
        }
        //  System.out.println(this.classes);
        // ora che ho raccolto tutte le statistiche
        // per ogni classe
        List<SolverPerformance> toExport = new ArrayList<>();
        for (InstanceClass instanceClass : this.classes) {
            // costruisco ciò che mi interessa, ovvero le prestazioni dei solver
            List<Solver> solvers = new ArrayList<>();
            solvers.addAll(problem.getOptimumSolvers());
            solvers.addAll(problem.getRelaxedSolvers());
            for (Solver solver : solvers) {
                SolverPerformance solverPerf = new SolverPerformance();
                solverPerf.setSolver(solver.getSolverName());
                // SETTARE: AVGTIME, AVGERRORS, OPTIMALS_COUNT
                double avgTime = 0;
                double avgAbsErr = 0;
                double avgRelErr = 0;
                int optimalsCount = 0;
                for (Instance report : instanceClass.getInstances()) {
                    for (InstanceExecResult result : report.getResults()) {
                        if (result.getSolverName().equals(solver.getSolverName())) {
                            if (report.isOptimal()&& result.getSolution()==report.getOptimalOrEstimate()) optimalsCount++;
                            avgTime += result.getTime();
                            avgAbsErr += result.getSolution() - report.getOptimalOrEstimate();
                            avgRelErr += report.getOptimalOrEstimate() == 0 ? 0 : result.getSolution() / report.getOptimalOrEstimate();
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
                instanceClass.addSolverPerf(solverPerf);
            }
        }
        String name = problem.getName() == null ? "" : "-" + problem.getName() + "-";
        // System.out.println(this.classes);
        for (InstanceClass instanceClass : this.classes) {
            System.out.println(this.classes.size());
            //toExport.add(instanceClass.getSize());
            for (SolverPerformance s : instanceClass.getSolversPerfs())
                toExport.add(s);
        }
        CSVExporterPrinter.getSingletonInstance().convertAndExport(this.classes,
                "/output/report/report" + name + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")) + ".csv");
        System.out.println("Done.");
    }

    private String fetchInstClassAttribute(Object[] enumeration, String needle) {
        for (Object val : enumeration) {
            if (needle.contains(val.toString())) return val.toString();
        }
        return null;
    }

    private InstanceClass fetchClassIfAlreadyExistent(InstanceClass instanceClass) {
        for (InstanceClass iteratedClass : this.classes) {
            //NOTA: di proposito non uso equals()
            if (
                    iteratedClass.getSize() == (instanceClass.getSize()) &&
                            iteratedClass.getVariance() == (instanceClass.getVariance()) &&
                            iteratedClass.getReleaseTimes() == (instanceClass.getReleaseTimes())
            ) return iteratedClass;
        }
        this.classes.add(instanceClass);
        return instanceClass;
    }

    private InstanceClass obtainClass(String path) {
        // si occupa anche di aggiungere la classe alla lista, qualora non esistesse
        InstanceClass ret = new InstanceClass();
        ret.setSize(fetchInstClassAttribute(InstanceGenerator.Size.values(), path));
        ret.setVariance(fetchInstClassAttribute(InstanceGenerator.Variance.values(), path));
        ret.setReleaseTimes(fetchInstClassAttribute(InstanceGenerator.ReleaseTimes.values(), path));

        InstanceClass instanceClass = fetchClassIfAlreadyExistent(ret);
        //return instanceClass==null?ret:instanceClass;
        return instanceClass;
    }
}
