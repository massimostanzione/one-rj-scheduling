package it.uniroma2.dicii.amod.onerjscheduling.solvers;

import it.uniroma2.dicii.amod.onerjscheduling.utils.ExternalConfig;

public class AMPLGurobiSolver extends AMPLSolver {
    @Override
    protected String initAMPLImplSolverName() {
        return "gurobi";
    }

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
}
