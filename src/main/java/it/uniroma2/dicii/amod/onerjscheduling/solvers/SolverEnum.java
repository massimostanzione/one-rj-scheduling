package it.uniroma2.dicii.amod.onerjscheduling.solvers;

/**
 * Enumeration of all the solvers available.
 * See the following specific classes for further information.
 *
 * @see AMPLSolver
 * @see AMPLGurobiSolver
 * @see AMPLCplexSolver
 * @see BnBSolver
 * @see BnBFullSolver
 * @see BnBForwardSolver
 * @see BnBFIFOSolver
 * @see BnBLLBSolver
 */
public enum SolverEnum {
    AMPL_GUROBI,
    AMPL_CPLEX,
    BRANCH_AND_BOUND_FULL,
    BRANCH_AND_BOUND_FORWARD,
    BRANCH_AND_BOUND_FIFO,
    BRANCH_AND_BOUND_LLB
}
