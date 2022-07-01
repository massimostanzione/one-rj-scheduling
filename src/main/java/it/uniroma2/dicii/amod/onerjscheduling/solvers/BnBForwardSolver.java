package it.uniroma2.dicii.amod.onerjscheduling.solvers;

import it.uniroma2.dicii.amod.onerjscheduling.entities.BnBProblem;
import it.uniroma2.dicii.amod.onerjscheduling.scheduling.Schedule;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

public class BnBForwardSolver extends BnBSolver {

    @Override
    public int solveExecutive(Instant start) {
        this.openBnBProblems = new ArrayList<>();
        this.incumbent = Integer.MAX_VALUE;
        this.globLB = Integer.MAX_VALUE;
        this.statuses = new HashMap<>();

        Schedule initalSchedule = new Schedule();
        BnBProblem rootBnBProblem = new BnBProblem(initalSchedule);
        this.openBnBProblems.add(rootBnBProblem);

        while (openBnBProblems.size() > 0) {
            if (checkTimeout(start)) return -1;
            BnBProblem p = this.openBnBProblems.get(0);
            this.openBnBProblems.remove(p);
            p = this.examineProblem(p, rootBnBProblem);
            this.openBnBProblems.addAll(generateSubProblems(p, true));
            this.updateStatuses(p.getStatus());
        }
        return this.incumbent;
    }

    @Override
    public void setName() {
        this.name = SolverEnum.BRANCH_AND_BOUND_FORWARD;
    }
}
