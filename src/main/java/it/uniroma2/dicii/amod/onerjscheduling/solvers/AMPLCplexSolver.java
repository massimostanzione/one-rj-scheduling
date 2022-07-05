package it.uniroma2.dicii.amod.onerjscheduling.solvers;

public class AMPLCplexSolver extends AMPLSolver {
    @Override
    protected String getAMPLImplSolver() {
        return "cplex";
    }

    @Override
    protected void setName() {
        this.name = SolverEnum.AMPL_CPLEX;
    }
}
