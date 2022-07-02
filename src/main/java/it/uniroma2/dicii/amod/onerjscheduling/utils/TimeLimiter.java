package it.uniroma2.dicii.amod.onerjscheduling.utils;

import it.uniroma2.dicii.amod.onerjscheduling.solvers.AMPLSolver;
import it.uniroma2.dicii.amod.onerjscheduling.solvers.Solver;


public class TimeLimiter implements Runnable {

    Thread t;
    // to stop the thread
    private boolean exit;
    private Solver solver;

    public TimeLimiter(Solver solver) {
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

    public void stop(Solver solver)  {
        exit = true;
        if(solver instanceof AMPLSolver){
                solver.getAmplInstance().interrupt();
                solver.getAmplInstance().close();
                solver.initializeSolverParams();
        }
    }

    public int compute(Solver solver) {
        return solver.solveExecutive(null);
    }
}