package it.uniroma2.dicii.amod.onerjscheduling.solvers;

/**
 * <i>Branch-and-bound</i> "Full" solver.
 */
public class BnBFullSolver extends BnBNonBackwardSolver {
    public BnBFullSolver() {
        this.checkForExpansion = false;
    }

    @Override
    public SolverEnum initName() {
        return SolverEnum.BRANCH_AND_BOUND_FULL;
    }
}
