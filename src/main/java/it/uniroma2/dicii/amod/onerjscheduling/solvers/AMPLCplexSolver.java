package it.uniroma2.dicii.amod.onerjscheduling.solvers;

import it.uniroma2.dicii.amod.onerjscheduling.utils.ExternalConfig;

public class AMPLCplexSolver extends AMPLSolver {
    @Override
    protected String initSpecificSolverOptionsPrefix() {
        return "cplex_options";
    }

    @Override
    protected String initSpecificSolverOptions() {
        return "time="+ ExternalConfig.getSingletonInstance().getComputationTimeout()/1000;
    }

    @Override
    protected String initAMPLImplSolverName() {
        return "cplex";
    }

    @Override
    public SolverEnum initName() {
        return SolverEnum.AMPL_CPLEX;
    }
}
