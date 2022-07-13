package it.uniroma2.dicii.amod.onerjscheduling.solvers;

import it.uniroma2.dicii.amod.onerjscheduling.entities.BnBProblem;
import it.uniroma2.dicii.amod.onerjscheduling.entities.Instance;
import it.uniroma2.dicii.amod.onerjscheduling.entities.Job;
import it.uniroma2.dicii.amod.onerjscheduling.entities.output.InstanceExecResult;
import it.uniroma2.dicii.amod.onerjscheduling.objectfunctions.ObjectiveFunction;
import it.uniroma2.dicii.amod.onerjscheduling.scheduling.Schedule;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * <i>Branch-and-bound</i> solver, with <i>BFS ("non-backward")</i> tree visiting scheme.
 */
public abstract class BnBNonBackwardSolver extends BnBSolver {
    protected boolean checkForExpansion;

    @Override
    public InstanceExecResult solveExecutive(Instant start, ObjectiveFunction objFn, Instance instance) {
        while (this.openBnBProblems.size() > 0) {
            if (checkTimeout(start))
                return new InstanceExecResult(this.incumbent, this.globLB);
            BnBProblem p = this.openBnBProblems.get(0);
            this.openBnBProblems.remove(p);
            p = examineProblem(p, objFn);
            if (p.isOptimalByLB())
                return new InstanceExecResult(this.incumbent, this.globLB);
            this.openBnBProblems.addAll(this.generateSubProblems(p, this.checkForExpansion, start));
        }
        return new InstanceExecResult(this.incumbent, this.globLB);
    }

    @Override
    protected List<BnBProblem> generateSubProblems(BnBProblem p, boolean checkForExpansion, Instant start) {
        List<BnBProblem> ret = new ArrayList<>();
        if (p.isExpandable() || !checkForExpansion) {
            // exclude leaves level
            if (p.getFullInitialSchedule().getItems().size() < this.jobList.size() - 1) {
                this.jobList.sort(Comparator.comparing(Job::getId));
                for (Job j : this.jobList) {
                    if (!p.getFullInitialSchedule().contains(j)) {
                        Schedule s = new Schedule();
                        s.getItems().addAll(p.getFullInitialSchedule().getItems());
                        BnBProblem child = new BnBProblem(s, j);
                        ret.add(child);
                    }
                }
            }
            p.setExpanded();
        }
        return ret;
    }
}
