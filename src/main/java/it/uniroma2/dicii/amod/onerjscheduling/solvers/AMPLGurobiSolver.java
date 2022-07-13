package it.uniroma2.dicii.amod.onerjscheduling.solvers;

import it.uniroma2.dicii.amod.onerjscheduling.utils.ExternalConfig;

/**
 * Specific class for AMPL Gurobi solver.
 */
public class AMPLGurobiSolver extends AMPLSolver {
    @Override
    public SolverEnum initName() {
        return SolverEnum.AMPL_GUROBI;
    }

    @Override
    public String initSpecificSolverOptionsPrefix() {
        return "gurobi_options";
    }

    @Override
    public String initSpecificSolverOptions() {
        return "timelim=" + ExternalConfig.getSingletonInstance().getComputationTimeout() / 1000;
    }

    @Override
    protected String initAMPLImplSolverName() {
        return "gurobi";
    }
}
