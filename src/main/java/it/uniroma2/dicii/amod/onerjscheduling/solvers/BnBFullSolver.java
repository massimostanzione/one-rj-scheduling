package it.uniroma2.dicii.amod.onerjscheduling.solvers;

public class BnBFullSolver extends BnBNonBackwardSolver {
    public BnBFullSolver() {
        this.checkForExpansion=false;
    }

    @Override
    protected void setName() {
        this.name = SolverEnum.BRANCH_AND_BOUND_FULL;
    }
}
