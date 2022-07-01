package it.uniroma2.dicii.amod.onerjscheduling.solvers;

import it.uniroma2.dicii.amod.onerjscheduling.entities.BnBProblem;

public class BoundingInfo {
    private BnBProblem problem;
    private int incumbent;
    private int globLB;

    public BnBProblem getProblem() {
        return problem;
    }

    public void setProblem(BnBProblem problem) {
        this.problem = problem;
    }

    public int getIncumbent() {
        return incumbent;
    }

    public void setIncumbent(int incumbent) {
        this.incumbent = incumbent;
    }

    public int getGlobLB() {
        return globLB;
    }

    public void setGlobLB(int globLB) {
        this.globLB = globLB;
    }
}
