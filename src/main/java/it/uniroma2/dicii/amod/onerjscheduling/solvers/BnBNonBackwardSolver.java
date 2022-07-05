package it.uniroma2.dicii.amod.onerjscheduling.solvers;

import it.uniroma2.dicii.amod.onerjscheduling.entities.BnBProblem;
import it.uniroma2.dicii.amod.onerjscheduling.scheduling.Schedule;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class BnBNonBackwardSolver extends BnBSolver {
    protected boolean checkForExpansion;

    @Override
    public int solveExecutive(Instant start) {
        this.openBnBProblems = new ArrayList<>();
        this.incumbent = Integer.MAX_VALUE;
        this.globLB = Integer.MAX_VALUE;
        this.statuses = new HashMap<>();

        Schedule initalSchedule = new Schedule();
        BnBProblem rootBnBProblem = new BnBProblem(initalSchedule);
        this.openBnBProblems.add(rootBnBProblem);

        while (this.openBnBProblems.size() > 0) {
            if (checkTimeout(start)) return this.incumbent;
            BnBProblem p = this.openBnBProblems.get(0);
            this.openBnBProblems.remove(p);
            p = examineProblem(p, rootBnBProblem);
            this.openBnBProblems.addAll(generateSubProblems(p, this.checkForExpansion));
            this.updateStatuses(p.getStatus());
        }
        return this.incumbent;
    }
}
