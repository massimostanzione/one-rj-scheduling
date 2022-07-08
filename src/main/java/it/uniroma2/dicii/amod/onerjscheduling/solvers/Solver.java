package it.uniroma2.dicii.amod.onerjscheduling.solvers;

import it.uniroma2.dicii.amod.onerjscheduling.entities.DataInstance;
import it.uniroma2.dicii.amod.onerjscheduling.entities.ExecutionReportItem;
import it.uniroma2.dicii.amod.onerjscheduling.exceptions.InvalidFinalStatusException;
import it.uniroma2.dicii.amod.onerjscheduling.objectfunctions.ObjectFunction;
import it.uniroma2.dicii.amod.onerjscheduling.objectfunctions.ObjectFunctionEnum;
import it.uniroma2.dicii.amod.onerjscheduling.utils.ExternalConfig;

import java.time.Duration;
import java.time.Instant;

// vale per 1|r_j|f
public abstract class Solver {
    //protected String path;
    protected SolverEnum solverName;
    //ObjectFunction objFunction = null;

    public Solver() {
        this.solverName =this.initName();
    }
/*
    public ObjectFunction getObjFunction() {
        return objFunction;
    }

    public void setObjFunction(ObjectFunctionEnum objFunction) {
        ObjFunctionFactory factory = new ObjFunctionFactory();
        try {
            this.objFunction = factory.createObjFunction(objFunction);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/
    public abstract SolverEnum initName();

/*
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
*/
    // l'obiettivo è:
    // - far scendere l'UB globale sul LB globale (trovo l'OTTIMO)
    // oppure
    // - arrivare a non avere più problemi aperti (trovo un LB)
    public abstract ExecutionReportItem solveExecutive(Instant start, ObjectFunction objFn, DataInstance instance);

    public ExecutionReportItem solve(ObjectFunction objFn,DataInstance instance) {
        this.initializeSolverParams(instance);
       // ExecutionReportItem item = new ExecutionReportItem();
        //item.setDataPath(this.path);
        //item.setObjFunction(this.objFunction.getName());

        var ref = new Object() {
            int solution = -1;
        };
        Instant start = Instant.now();
        // no more need of the AMPLSolverTimeLimiter thread!
       /* if (this instanceof AMPLSolver) {
            System.out.println("lancio thread AMPL");
            try {
                AMPLSolverTimeLimiter t1 = new AMPLSolverTimeLimiter((AMPLSolver) this);
                ref.solution = t1.compute(this);
                t1.stop((AMPLSolver) this);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {*/
            ExecutionReportItem item = this.solveExecutive(start, objFn,instance);
        //}

        Instant end = Instant.now();
        item.setSolverName(this.solverName);

      /*  if (ref.solution == -1) {
            System.out.println("Timeout. Execution exceeded " + ExternalConfig.getSingletonInstance().getComputationTimeout() + " ms.\n");
        }*/
     //   item.setSolution(ref.solution);
     /*   if (ref.solution == -1)
            item.setTime(ExternalConfig.getSingletonInstance().getShowElapsedTimeOnTimeout() ?
                    Duration.between(start, end).toMillis() : -1);
        else {*/
            item.setTime(Duration.between(start, end).toMillis());
      //  }
        printStats();
        return item;
    }

    protected abstract void initializeSolverParams(DataInstance instance);

    protected abstract void printStats() ;
}
