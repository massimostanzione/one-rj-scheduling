package it.uniroma2.dicii.amod.onerjscheduling.solvers;

public class BnBForwardSolver extends BnBNonBackwardSolver {

    public BnBForwardSolver() {
        this.checkForExpansion=true;
    }

    @Override
    public SolverEnum initName() {
        return SolverEnum.BRANCH_AND_BOUND_FORWARD;
    }
}
