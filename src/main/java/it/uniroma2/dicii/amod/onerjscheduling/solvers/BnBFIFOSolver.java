package it.uniroma2.dicii.amod.onerjscheduling.solvers;

import it.uniroma2.dicii.amod.onerjscheduling.control.Scheduler;
import it.uniroma2.dicii.amod.onerjscheduling.entities.BnBProblem;
import it.uniroma2.dicii.amod.onerjscheduling.entities.Instance;
import it.uniroma2.dicii.amod.onerjscheduling.entities.output.InstanceExecResult;
import it.uniroma2.dicii.amod.onerjscheduling.objectfunctions.ObjectiveFunction;

import java.time.Instant;
import java.util.List;

/**
 * <i>Branch-and-bound</i> "FIFO" solver.
 */
public class BnBFIFOSolver extends BnBDFSSolver {

    @Override
    public SolverEnum initName() {
        return SolverEnum.BRANCH_AND_BOUND_FIFO;
    }

    @Override
    public InstanceExecResult solveExecutive(Instant start, ObjectiveFunction objFn, Instance instances) {
        Scheduler sch = new Scheduler();
        while (this.openBnBProblems.size() > 0) {
            if (checkTimeout(start))
                return new InstanceExecResult(this.incumbent, this.globLB);
            BnBProblem p = this.openBnBProblems.get(0);
            this.openBnBProblems.remove(p);
            // node could be already visited if we are going up into the branching tree
            if (!p.isVisited()) {
                p = this.examineProblem(p, objFn);
                if (p.isOptimalByLB())
                    return new InstanceExecResult(this.incumbent, this.globLB);
                if (!sch.scheduleIsInProblemListByFullInitSchedule(p.getFullInitialSchedule(), this.potentiallyExpandableInCurrentLevel))
                    this.potentiallyExpandableInCurrentLevel.add(p);
            }
            if (!p.isClosed()) {
                List<BnBProblem> sub = this.generateSubProblems(p, start);
                if (sub != null)
                    this.openBnBProblems.addAll(sub);
            }
        }
        return new InstanceExecResult(this.incumbent, this.globLB);
    }
}
