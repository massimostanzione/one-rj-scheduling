package it.uniroma2.dicii.amod.onerjscheduling.solvers;

import it.uniroma2.dicii.amod.onerjscheduling.control.Scheduler;
import it.uniroma2.dicii.amod.onerjscheduling.entities.BnBProblem;
import it.uniroma2.dicii.amod.onerjscheduling.entities.Instance;
import it.uniroma2.dicii.amod.onerjscheduling.entities.Job;
import it.uniroma2.dicii.amod.onerjscheduling.scheduling.Schedule;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * <i>Branch-and-bound</i> solver, with <i>DFS</i> tree visiting scheme.
 */
public abstract class BnBDFSSolver extends BnBSolver {
    protected List<BnBProblem> potentiallyExpandableInCurrentLevel;

    protected List<BnBProblem> generateSubProblems(BnBProblem p, Instant start) {
        return this.generateSubProblems(p, true, start);
    }

    @Override
    protected void initializeSolverParams(Instance instance) {
        super.initializeSolverParams(instance);
        this.potentiallyExpandableInCurrentLevel = new ArrayList<>();
        this.potentiallyExpandableInCurrentLevel.add(this.openBnBProblems.get(0));
    }

    @Override
    protected List<BnBProblem> generateSubProblems(BnBProblem p, boolean checkForExpansion, Instant start) {
        //System.out.println("cerco di generare sottoproblemi per "+p);
        List<BnBProblem> ret = new ArrayList<>();
        Scheduler sch = new Scheduler();
        this.jobList.sort(Comparator.comparing(Job::getId));
        if (checkTimeout(start))
            return ret;
        BnBProblem next = null;
        // if problem is not a leaf, we still have an opportunity to expand it
        if (!isLeaf(p)) {
            // if the problem is expandable, program already visited it,
            // so we can try to go even deeper,
            // whilst if it is fathomed, we only can go up of a level
            if (p.isWaitingForFurtherProcessing()) {
                // generate sequence
                Schedule s = new Schedule();
                for (Job j : this.jobList) {
                    if (checkTimeout(start))
                        return ret;
                    if (!p.getFullInitialSchedule().contains(j)) {
                        s.getItems().removeAll(s.getItems());
                        s.getItems().addAll(p.getFullInitialSchedule().getItems());
                        next = new BnBProblem(s, j);
                        // looking for a non-examined schedule
                        if (!sch.scheduleIsInProblemListByFullInitSchedule(next.getFullInitialSchedule(), this.potentiallyExpandableInCurrentLevel)) {
                            // going deeper with next subroblem
                            if (checkTimeout(start)) return ret;
                            ret.add(next);
                        }
                    }
                }
                // if some subproblem is still open, it means that some schedule is yet to explore,
                // but we have already genereated all of it, so return them
                if (ret.size() > 0) {
                    p.setExpanded();
                    return ret;
                }
                // here we don't have further schedule to examine, so we go up of one level
            }
            // problem was non-expandable, so we need to go up of one level
        }
        // problem was leaf, so we need to go up of one level

        // ... so we have to go to the previous level
        // checking into te list of visited nodes, from the most recent, going backwards
        for (int i = this.potentiallyExpandableInCurrentLevel.size() - 1; i >= 0; i--) {
            // if it is expandable and it is at the previous level
            if (this.potentiallyExpandableInCurrentLevel.get(i).isWaitingForFurtherProcessing()
                    && isSuperiorLevel(this.potentiallyExpandableInCurrentLevel.get(i), p)) {
                ret.add(this.potentiallyExpandableInCurrentLevel.get(i));
            }
        }
        // here we don't have anything to examine further.
        p.setExpanded();
        return ret;
    }

    private boolean isSuperiorLevel(BnBProblem a, BnBProblem b) {
        return a.getFullInitialSchedule().getItems().size() <=
                b.getFullInitialSchedule().getItems().size() - 1;
    }

    private boolean isLeaf(BnBProblem p) {
        return p.getFullInitialSchedule().getItems().size() == this.jobList.size() - 1;
    }
}
