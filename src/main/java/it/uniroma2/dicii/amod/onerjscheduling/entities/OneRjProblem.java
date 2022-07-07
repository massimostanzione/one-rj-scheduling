package it.uniroma2.dicii.amod.onerjscheduling.entities;

import it.uniroma2.dicii.amod.onerjscheduling.objectfunctions.ObjectFunction;
import it.uniroma2.dicii.amod.onerjscheduling.objectfunctions.ObjectFunctionEnum;
import it.uniroma2.dicii.amod.onerjscheduling.solvers.Solver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class models a 1|r_j|f problem, where f is an object function described by <code>objectFunction</code>.
 */
public class OneRjProblem {
    private String name;    // optional
    private ObjectFunction objectFunction;
    private List<DataInstance> instances = new ArrayList<>();
    private List<Solver> optimumSolvers = new ArrayList<>();
    private List<Solver> relaxedSolvers = new ArrayList<>();
    //private List<ExecutionReportItem> results = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ObjectFunction getObjectFunction() {
        return objectFunction;
    }

    public void setObjectFunction(ObjectFunction objectFunction) {
        this.objectFunction = objectFunction;
    }

       public List<DataInstance> getInstances() {
           return instances;
       }

       public void setInstances(List<DataInstance> instances) {
           this.instances = instances;
       }

    public void addInstance(DataInstance instance) {
        this.instances.add(instance);
    }

    public List<Solver> getOptimumSolvers() {
        return optimumSolvers;
    }

    public void setOptimumSolvers(List<Solver> optimumSolvers) {
        this.optimumSolvers = optimumSolvers;
    }

    public void addOptimumSolver(Solver optimumSolver) {
        this.optimumSolvers.add(optimumSolver);
    }

    public List<Solver> getRelaxedSolvers() {
        return relaxedSolvers;
    }

    public void setRelaxedSolvers(List<Solver> relaxedSolvers) {
        this.relaxedSolvers = relaxedSolvers;
    }

    public void addRelaxedSolver(Solver relaxedSolver) {
        this.relaxedSolvers.add(relaxedSolver);
    }

    /*
        public List<ExecutionReportItem> getResults() {
            return results;
        }

        public void setResults(List<ExecutionReportItem> results) {
            this.results = results;
        }
    */
    public void loadInstanceDirectory(String path) {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            //this.addInstance(listOfFiles[i].toString());
            if(!listOfFiles[i].isDirectory())
            this.addInstance(new DataInstance(listOfFiles[i].toString()));
        }
    }
}
