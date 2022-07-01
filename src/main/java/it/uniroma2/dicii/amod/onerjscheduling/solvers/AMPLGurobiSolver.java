package it.uniroma2.dicii.amod.onerjscheduling.solvers;

public class AMPLGurobiSolver extends AMPLSolver {
    @Override
    protected String getAMPLImplSolver() {
        return "gurobi";
    }

    @Override
    public void setName() {
        this.name = SolverEnum.AMPL_GUROBI;
    }
}
