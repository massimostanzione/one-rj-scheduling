package it.uniroma2.dicii.amod.onerjscheduling.control;

import it.uniroma2.dicii.amod.onerjscheduling.entities.Instance;
import it.uniroma2.dicii.amod.onerjscheduling.entities.output.InstanceExecResult;
import it.uniroma2.dicii.amod.onerjscheduling.objectfunctions.ObjectFunction;
import it.uniroma2.dicii.amod.onerjscheduling.solvers.Solver;

import java.util.List;

public class SolverThread implements Runnable {
    private final Solver solver;
    private final ObjectFunction objectFunction;
    private final Instance instance;
    private InstanceExecResult result;

    public SolverThread(Solver solver, ObjectFunction objectFunction, Instance instance) {
        this.solver = solver;
        this.objectFunction = objectFunction;
        this.instance = instance;
    }

    @Override
    public void run() {

        InstanceExecResult item = this.solver.solve(this.objectFunction, this.instance);
        item.setPath(this.instance.getPath());
        this.result = item;
    }

    public InstanceExecResult getResult() {
        return this.result;
    }
}
