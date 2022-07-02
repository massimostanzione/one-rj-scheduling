package it.uniroma2.dicii.amod.onerjscheduling.solvers;

import it.uniroma2.dicii.amod.onerjscheduling.entities.ExecutionReportItem;
import it.uniroma2.dicii.amod.onerjscheduling.objectfunctions.ObjFunctionFactory;
import it.uniroma2.dicii.amod.onerjscheduling.objectfunctions.ObjectFunction;
import it.uniroma2.dicii.amod.onerjscheduling.objectfunctions.ObjectFunctionEnum;
import it.uniroma2.dicii.amod.onerjscheduling.utils.ExternalConfig;
import it.uniroma2.dicii.amod.onerjscheduling.utils.AMPLSolverTimeLimiter;

import java.time.Duration;
import java.time.Instant;

// vale per 1|r_j|f
public abstract class Solver {
    protected String path;
    protected SolverEnum name;
    ObjectFunction objFunction = null;

    public Solver() {
        this.setName();
    }


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

    public SolverEnum getName() {
        return name;
    }

    public abstract void setName();

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    // l'obiettivo è:
    // - far scendere l'UB globale sul LB globale (trovo l'OTTIMO)
    // oppure
    // - arrivare a non avere più problemi aperti (trovo un LB)
    public abstract int solveExecutive(Instant start);

    public ExecutionReportItem solve() {
        initializeSolverParams();
        ExecutionReportItem item = new ExecutionReportItem();
        item.setDataPath(this.path);
        item.setSolverName(this.name);
        item.setObjFunction(this.objFunction.getName());

        var ref = new Object() {
            int solution = -1;
        };
        Instant start = Instant.now();
        if (this instanceof AMPLSolver) {
            System.out.println("lancio thread AMPL");
            try {
                AMPLSolverTimeLimiter t1 = new AMPLSolverTimeLimiter((AMPLSolver) this);
                ref.solution = t1.compute(this);
                t1.stop((AMPLSolver) this);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            ref.solution = this.solveExecutive(start);
        }

        Instant end = Instant.now();

        if (ref.solution == -1) {
            System.out.println("Timeout. Execution exceeded " + ExternalConfig.getSingletonInstance().getComputationTimeout() + " ms.\n");
        }
        item.setSolution(ref.solution);
        // TODO se mantenere comunque indicazione del tempo quando va in timeout
        //item.setTime(ref.solution == -1 ? -1 : Duration.between(start, end).toMillis());
        item.setTime(Duration.between(start, end).toMillis());
        printStats();
        return item;
    }

    public abstract void initializeSolverParams();

    protected abstract void printStats();
}
