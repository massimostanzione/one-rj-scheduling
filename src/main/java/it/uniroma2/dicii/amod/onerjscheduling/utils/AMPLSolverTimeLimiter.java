package it.uniroma2.dicii.amod.onerjscheduling.utils;

import it.uniroma2.dicii.amod.onerjscheduling.solvers.AMPLSolver;
import it.uniroma2.dicii.amod.onerjscheduling.solvers.Solver;


public class AMPLSolverTimeLimiter implements Runnable {

    Thread t;
    // to stop the thread
    private boolean exit;
    private AMPLSolver solver;

    public AMPLSolverTimeLimiter(AMPLSolver solver) {
        t = new Thread(this);
        exit = false;
        this.solver=solver;
        t.start();
    }

    public void run() {
        while (!exit) {
            try {
                Thread.sleep(ExternalConfig.getSingletonInstance().getComputationTimeout());
                stop(this.solver);
            } catch (InterruptedException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stop(AMPLSolver solver)  {
        exit = true;
                solver.getAmplInstance().interrupt();
                solver.getAmplInstance().close();
                solver.initializeSolverParams(null);
    }

    public int compute(Solver solver) {
        //return solver.solveExecutive(null, null, null);
        return -1;
    }
}