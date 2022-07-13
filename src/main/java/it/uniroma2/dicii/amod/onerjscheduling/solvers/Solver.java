package it.uniroma2.dicii.amod.onerjscheduling.solvers;

import it.uniroma2.dicii.amod.onerjscheduling.entities.Instance;
import it.uniroma2.dicii.amod.onerjscheduling.entities.output.InstanceExecResult;
import it.uniroma2.dicii.amod.onerjscheduling.objectfunctions.ObjectiveFunction;
import it.uniroma2.dicii.amod.onerjscheduling.utils.ExternalConfig;
import it.uniroma2.dicii.amod.onerjscheduling.utils.ProblemStatus;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstraction for a 1|r_j|f problem solver.
 * To be further specialized.
 */
public abstract class Solver {
    protected SolverEnum solverName;
    protected Map<ProblemStatus, Integer> statuses;

    public Solver() {
        this.solverName = this.initName();
        this.statuses = new HashMap<>();
    }

    public abstract SolverEnum initName();

    public SolverEnum getSolverName() {
        return solverName;
    }

    public InstanceExecResult solve(ObjectiveFunction objFn, Instance instance) {
        this.initializeSolverParams(instance);

        Instant start = Instant.now();
        InstanceExecResult item = this.solveExecutive(start, objFn, instance);
        Instant end = Instant.now();
        item.setSolverName(this.solverName);
        item.setTime(Duration.between(start, end).toMillis());
        item.setStatuses(this.statuses);
        int nodesCtr = 0;
        for (int val : item.getStatuses().values()) {
            nodesCtr += val;
        }
        item.setTotalVisitedNodes(nodesCtr);
        boolean timeout = item.getTime() >= ExternalConfig.getSingletonInstance().getComputationTimeout();
        printStats(timeout);
        return item;
    }

    protected abstract void initializeSolverParams(Instance instance);

    protected abstract void printStats(boolean timeout);

    public abstract InstanceExecResult solveExecutive(Instant start, ObjectiveFunction objFn, Instance instance);

}
