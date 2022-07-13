package it.uniroma2.dicii.amod.onerjscheduling.entities;

import it.uniroma2.dicii.amod.onerjscheduling.dao.DataDAO;
import it.uniroma2.dicii.amod.onerjscheduling.entities.output.InstanceExecResult;
import it.uniroma2.dicii.amod.onerjscheduling.utils.ExternalConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * A single job instance, also working as <i>container</i> for the execution results <code>InstanceExecResults</code>
 * of all the solver for the specific istance.
 */
public class Instance {
    private final String path;
    private final List<Job> jobList;
    private final List<InstanceExecResult> results;
    private int bestObtainedSolution;
    private int bnbRootLB;
    private boolean isOptimal;

    public Instance(String path) {
        this.path = path;
        this.bestObtainedSolution = Integer.MAX_VALUE;
        this.bnbRootLB = 0;
        this.isOptimal = false;
        this.results = new ArrayList<>();
        this.jobList = this.extractJobs();
    }

    private List<Job> extractJobs() {
        return DataDAO.getSingletonInstance().getDataInstances(this.path);
    }

    public int getBestObtainedSolution() {
        return this.bestObtainedSolution;
    }

    public int getOptimalOrEstimate() {
        return this.isOptimal ? this.bestObtainedSolution : this.bnbRootLB;
    }

    public boolean isOptimal() {
        return isOptimal;
    }

    public String getPath() {
        return path;
    }

    public List<Job> getJobList() {
        return jobList;
    }

    public List<InstanceExecResult> getResults() {
        return results;
    }

    /**
     * Append the single <code>item</code> execution result, and also process it in order to update
     * some attributes about the specific instance.
     *
     * @param item execution result to be appended.
     */
    public void addResult(InstanceExecResult item) {
        // append the item to the results list...
        this.results.add(item);
        // ... but also process it
        // se bnb salvo il risultato, perché se non riesco a trovare l'ottimo uso quello per
        // stimare l'errore, in assenza dell'ottimo
        if (item.getRootLB() != 0) {
            if (this.bnbRootLB != 0 && item.getRootLB() != this.bnbRootLB) {
                throw new RuntimeException("not valid, current LB = " + this.bnbRootLB + " vs. obtained " + item.getRootLB());
            }
            this.bnbRootLB = item.getRootLB();
        }
        // check for timeout
        if (item.getTime() < ExternalConfig.getSingletonInstance().getComputationTimeout()) {
            this.isOptimal = true;
        }
        // check if better result
        if (item.getSolution() < this.bestObtainedSolution && item.getSolution() > 0)//this.bnbRootLB)
            this.bestObtainedSolution = item.getSolution();
    }

    @Override
    public String toString() {
        return "\npath='" + path + '\'' +
                ",\n bestSolution=" + bestObtainedSolution +
                ",\n isOptimal=" + isOptimal;
    }
}
