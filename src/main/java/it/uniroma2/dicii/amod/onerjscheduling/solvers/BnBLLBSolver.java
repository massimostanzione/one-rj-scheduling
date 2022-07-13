package it.uniroma2.dicii.amod.onerjscheduling.solvers;

import it.uniroma2.dicii.amod.onerjscheduling.control.Scheduler;
import it.uniroma2.dicii.amod.onerjscheduling.entities.BnBProblem;
import it.uniroma2.dicii.amod.onerjscheduling.entities.Instance;
import it.uniroma2.dicii.amod.onerjscheduling.entities.output.InstanceExecResult;
import it.uniroma2.dicii.amod.onerjscheduling.objectfunctions.ObjectiveFunction;

import java.time.Instant;
import java.util.List;

/**
 * <i>Branch-and-bound</i> "Least Lower Bound" solver.
 */
public class BnBLLBSolver extends BnBDFSSolver {

    @Override
    public SolverEnum initName() {
        return SolverEnum.BRANCH_AND_BOUND_LLB;
    }

    @Override
    public InstanceExecResult solveExecutive(Instant start, ObjectiveFunction objFn, Instance instance) {
        Scheduler sch = new Scheduler();
        // look at list like a branching tree level
        int min;
        while (this.openBnBProblems.size() > 0) {
            if (checkTimeout(start))
                return new InstanceExecResult(this.incumbent, this.globLB);
            min = Integer.MAX_VALUE;
            int res = -1;
            for (BnBProblem p : this.openBnBProblems) {
                if (checkTimeout(start))
                    return new InstanceExecResult(this.incumbent, this.globLB);
                if (!p.isVisited())
                    p = this.examineProblem(p, objFn);
                if (checkTimeout(start) || p.isOptimalByLB())
                    return new InstanceExecResult(this.incumbent, this.globLB);
                res = p.getSolution();
                if (p.isWaitingForFurtherProcessing()) {
                    if (!sch.scheduleIsInProblemListByFullInitSchedule(p.getFullInitialSchedule(), this.potentiallyExpandableInCurrentLevel))
                        this.potentiallyExpandableInCurrentLevel.add(p);
                    if (res < min && res != -1) min = res;
                    if (!this.potentiallyExpandableInCurrentLevel.contains(p)) {
                        this.potentiallyExpandableInCurrentLevel.add(p);
                    }
                }
            }
            // update the level
            this.openBnBProblems.removeAll(this.openBnBProblems);
            for (BnBProblem p : this.potentiallyExpandableInCurrentLevel) {
                if (checkTimeout(start))
                    return new InstanceExecResult(this.incumbent, this.globLB);
                if (p.getSolution().equals(min) || p.isRoot()) {
                    List<BnBProblem> sub = this.generateSubProblems(p, start);
                    if (checkTimeout(start))
                        return new InstanceExecResult(this.incumbent, this.globLB);
                    if (sub != null) {
                        this.openBnBProblems.addAll(sub);
                    }
                    p.setExpanded();
                } else {
                    if (p.isWaitingForFurtherProcessing()) {
                        this.openBnBProblems.add(p);
                    }
                }
            }
        }
        return new InstanceExecResult(this.incumbent, this.globLB);
    }
}
