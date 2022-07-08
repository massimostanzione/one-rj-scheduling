package it.uniroma2.dicii.amod.onerjscheduling.entities;

import it.uniroma2.dicii.amod.onerjscheduling.dao.DataDAO;
import it.uniroma2.dicii.amod.onerjscheduling.entities.output.InstanceExecResult;
import it.uniroma2.dicii.amod.onerjscheduling.utils.ExternalConfig;

import java.util.ArrayList;
import java.util.List;

// è il contenitore dei risultati di puiù solver sulla stessa istanza
public class Instance {
    private final String path;
    private final List<Job> jobList;
    private int bestObtainedSolution;
    private int bnbRootLB;
    private boolean isOptimal;
    private List<InstanceExecResult> results;

    public Instance(String path) {
        this.path = path;
        this.bestObtainedSolution = Integer.MAX_VALUE;
        this.bnbRootLB = 0;
        this.isOptimal = false;
        this.results = new ArrayList<>();
        this.jobList = this.extractJobs();
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

    /*
        public void setJobList(List<Job> jobList) {
            this.jobList = jobList;
        }
    */
    public List<InstanceExecResult> getResults() {
        return results;
    }

    public void setResults(List<InstanceExecResult> results) {
        this.results = results;
    }

    public void addResult(InstanceExecResult item) {
        // append the item to the results list...
        this.results.add(item);
        // ... but also process it
        // se bnb salvo il risultato, perché se non riesco a trovare l'ottimo uso quello per
        // stimare l'errore, in assenza dell'ottimo
        this.bnbRootLB = item.getRootLB();// TODO controllo su ev. già esistente che non può essere diverso
        // check for timeout
        if (item.getTime() <= ExternalConfig.getSingletonInstance().getComputationTimeout()) {
            this.isOptimal = true;
        }
        // check if better result
        if (item.getSolution() < this.bestObtainedSolution)
            this.bestObtainedSolution = item.getSolution();
    }

    private List<Job> extractJobs() {
        return DataDAO.getSingletonInstance().getDataInstances(this.path);
    }

    @Override
    public String toString() {
        return "DataInstance{" +
                "\npath='" + path + '\'' +
                ",\n jobList=" + jobList +
                ",\n bestSolution=" + bestObtainedSolution +
                ",\n isOptimal=" + isOptimal +
                ",\n results=" + results +
                '}';
    }
}
