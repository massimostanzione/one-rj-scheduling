package it.uniroma2.dicii.amod.onerjscheduling.control;

import it.uniroma2.dicii.amod.onerjscheduling.entities.Instance;
import it.uniroma2.dicii.amod.onerjscheduling.entities.output.InstanceExecResult;
import it.uniroma2.dicii.amod.onerjscheduling.objectfunctions.ObjectiveFunction;
import it.uniroma2.dicii.amod.onerjscheduling.solvers.Solver;

/**
 * Thread used to run in a parallel way the execution of the most time-requiring solvers
 */
public class SolverThread implements Runnable {
    private final Solver solver;
    private final ObjectiveFunction objectiveFunction;
    private final Instance instance;
    private InstanceExecResult result;

    public SolverThread(Solver solver, ObjectiveFunction objectiveFunction, Instance instance) {
        this.solver = solver;
        this.objectiveFunction = objectiveFunction;
        this.instance = instance;
    }

    /**
     * Obtain the final solver result, whether timeout is reached or not
     *
     * @return result obtained from the solver
     */
    public InstanceExecResult getResult() {
        return this.result;
    }

    @Override
    public void run() {
        InstanceExecResult item = this.solver.solve(this.objectiveFunction, this.instance);
        item.setPath(this.instance.getPath());
        this.result = item;
    }
}
