package it.uniroma2.dicii.amod.onerjscheduling.solvers;

import it.uniroma2.dicii.amod.onerjscheduling.entities.BnBProblem;
import it.uniroma2.dicii.amod.onerjscheduling.entities.Instance;
import it.uniroma2.dicii.amod.onerjscheduling.entities.Job;
import it.uniroma2.dicii.amod.onerjscheduling.exceptions.InconsistentStatusException;
import it.uniroma2.dicii.amod.onerjscheduling.exceptions.InvalidFinalStatusException;
import it.uniroma2.dicii.amod.onerjscheduling.objectfunctions.ObjectiveFunction;
import it.uniroma2.dicii.amod.onerjscheduling.scheduling.Schedule;
import it.uniroma2.dicii.amod.onerjscheduling.utils.ExternalConfig;
import it.uniroma2.dicii.amod.onerjscheduling.utils.ProblemStatus;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static it.uniroma2.dicii.amod.onerjscheduling.utils.ProblemStatus.*;

/**
 * <i>Branch-and-bound</i> solver, to be further specialized.
 */
public abstract class BnBSolver extends Solver {
    protected List<Job> jobList;
    protected List<BnBProblem> openBnBProblems;
    protected int incumbent;
    protected int globLB;
    protected List<BnBProblem> allNodes;

    protected abstract List<BnBProblem> generateSubProblems(BnBProblem p, boolean checkForExpansion, Instant start);

    protected BnBProblem examineProblem(BnBProblem p, ObjectiveFunction objFn) {
        boolean isFullSolver = this instanceof BnBFullSolver;
        p.setStatus(PROCESSING);
        if (checkDominance(p, this.jobList) && !isFullSolver) {
            p.setStatus(FATHOMED_DOMINANCE);
            p.setSolution(-1);
        } else {
            p = this.computeObjFnValue(p, objFn);
            int res = p.getSolution();
            if (p.isRoot()) this.globLB = res;
            if (res >= this.incumbent && !isFullSolver) {
                p.setStatus(FATHOMED_BOUNDING);
            }
            if (res < this.incumbent && p.isFeasible()) {
                this.incumbent = res;
                //global LB is kept fixed
                if (this.incumbent == this.globLB && !isFullSolver) {
                    p.setStatus(OPTIMAL_REACHED);
                }
            }
            // at this point the node can be marked as expandable
            if (p.getStatus() == PROCESSING) {
                p.setStatus(EXPANDABLE);
            }
        }
        this.recordForStats(p);
        return p;
    }

    protected static boolean checkDominance(BnBProblem p, List<Job> jobList) {
        // check if a job can "fit" between te last of the fixed schedule and the last who was added
        Schedule s = p.getFullInitialSchedule();
        Job lastAddedJob = p.getLastAddedJob();
        if (lastAddedJob != null) {
            for (Job j : getJobsNotInSchedule(jobList, s)) {
                if (lastAddedJob.getReleaseDate() >= Math.max(j.getReleaseDate(), (p.getInitialSchedule().getCompletionTime())) + j.getProcessingTime()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static List<Job> getJobsNotInSchedule(List<Job> jobList, Schedule s) {
        List<Job> ret = new ArrayList<>();
        for (Job j : jobList) {
            if (!s.contains(j)) {
                ret.add(j);
            }
        }
        return ret;
    }

    protected BnBProblem computeObjFnValue(BnBProblem p, ObjectiveFunction objFn) {
        p.setFinalSchedule(objFn.computeRelaxedSchedule(p.getFullInitialSchedule(), this.jobList));
        p.setFeasible(!p.getFinalSchedule().isPreempted());
        p.setSolution(objFn.compute(p.getFinalSchedule()));
        return p;
    }

    protected void recordForStats(BnBProblem p) {
        if (this.allNodes.contains(p)) {
            for (BnBProblem iterated : this.allNodes) {
                if (iterated.equals(p)) iterated.setStatus(p.getStatus());
            }
        } else this.allNodes.add(p);
    }

    public List<Job> getJobList() {
        return this.jobList;
    }

    public void setJobList(List<Job> jobList) {
        this.jobList = jobList;
    }

    protected boolean checkTimeout(Instant start) {
        return Duration.between(start, Instant.now()).toMillis() >= ExternalConfig.getSingletonInstance().getComputationTimeout();
    }

    private long computeNodesNo(int size) {
        // NOTICE: could return overflown values!
        long sum = size, incr = size;
        for (int i = size - 1; i >= 2; i--) {
            incr = incr * i;
            sum += incr;
        }
        // add root node
        return sum + 1;
    }

    @Override
    protected void initializeSolverParams(Instance instance) {
        this.jobList = instance.getJobList();
        this.allNodes = new ArrayList<>();
        this.openBnBProblems = new ArrayList<>();
        this.incumbent = Integer.MAX_VALUE;
        this.globLB = Integer.MAX_VALUE;
        this.statuses = new HashMap<>();

        Schedule initalSchedule = new Schedule();
        BnBProblem rootBnBProblem = new BnBProblem(initalSchedule);
        this.openBnBProblems.add(rootBnBProblem);
    }

    @Override
    protected void printStats(boolean timeout) {
        for (ProblemStatus status : ProblemStatus.values()) {
            for (BnBProblem p : this.allNodes) {
                if (!p.isClosed() && !timeout)
                    throw new InvalidFinalStatusException(p);
                if (p.getStatus() == status) {
                    if (this instanceof BnBFullSolver && status != EXPANDED)
                        throw new InconsistentStatusException("Found " + status + " in an BnBFullSolver execution.");
                    if (this.statuses.containsKey(status))
                        this.statuses.put(status, this.statuses.get(status) + 1);
                    else
                        this.statuses.put(status, 1);
                }
            }
        }
        System.out.println("LB in root node = " + this.globLB);
        int sum = 0;
        for (int val : this.statuses.values()) {
            sum += val;
        }
        System.out.println(sum + " tree nodes visited");
        System.out.println(this.statuses);
        System.out.println("\n");
    }
}
