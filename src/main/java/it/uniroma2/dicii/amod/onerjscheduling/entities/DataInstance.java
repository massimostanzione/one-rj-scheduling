package it.uniroma2.dicii.amod.onerjscheduling.entities;

import it.uniroma2.dicii.amod.onerjscheduling.dao.DataDAO;
import it.uniroma2.dicii.amod.onerjscheduling.utils.ExternalConfig;

import java.util.ArrayList;
import java.util.List;

public class DataInstance {
    private final String path;
    private List<Job> jobList;
    private int bestSolution;
    private boolean isOptimal;
    private List<ExecutionReportItem> results;

    public DataInstance(String path) {
        this.path = path;
        this.bestSolution = Integer.MAX_VALUE;
        this.isOptimal = false;
        this.results=new ArrayList<>();
        this.jobList=this.extractJobs();
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
    public List<ExecutionReportItem> getResults() {
        return results;
    }

    public void setResults(List<ExecutionReportItem> results) {
        this.results = results;
    }

    public void addResult(ExecutionReportItem item) {
        // append the item to the results list...
        this.results.add(item);
        // ... but also process it
        // check for timeout
        if (item.getTime() <= ExternalConfig.getSingletonInstance().getComputationTimeout()) {
            this.isOptimal = true;
        }
        // check if better result
        if (item.getSolution() < this.bestSolution)
            this.bestSolution = item.getSolution();
    }

    private List<Job> extractJobs() {
        return DataDAO.getSingletonInstance().getDataInstances(this.path);
    }

    @Override
    public String toString() {
        return "DataInstance{" +
                "\npath='" + path + '\'' +
                ",\n jobList=" + jobList +
                ",\n bestSolution=" + bestSolution +
                ",\n isOptimal=" + isOptimal +
                ",\n results=" + results +
                '}';
    }
}
