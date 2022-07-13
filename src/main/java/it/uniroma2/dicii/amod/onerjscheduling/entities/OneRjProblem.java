package it.uniroma2.dicii.amod.onerjscheduling.entities;

import it.uniroma2.dicii.amod.onerjscheduling.objectfunctions.ObjectiveFunction;
import it.uniroma2.dicii.amod.onerjscheduling.solvers.Solver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class models a 1|r_j|f problem, where f is an object function described by <code>objectFunction</code>.
 */
public class OneRjProblem {
    private final List<Instance> instances = new ArrayList<>();
    private final List<Solver> optimumSolvers = new ArrayList<>();
    private final List<Solver> relaxedSolvers = new ArrayList<>();
    private String name;                                        // optional
    private ObjectiveFunction objectiveFunction;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ObjectiveFunction getObjectFunction() {
        return objectiveFunction;
    }

    public void setObjectFunction(ObjectiveFunction objectiveFunction) {
        this.objectiveFunction = objectiveFunction;
    }

    public List<Instance> getInstances() {
        return instances;
    }

    public List<Solver> getOptimumSolvers() {
        return optimumSolvers;
    }

    public void addOptimumSolver(Solver optimumSolver) {
        this.optimumSolvers.add(optimumSolver);
    }

    public List<Solver> getRelaxedSolvers() {
        return relaxedSolvers;
    }

    public void addRelaxedSolver(Solver relaxedSolver) {
        this.relaxedSolvers.add(relaxedSolver);
    }

    /**
     * Load and parse all the CSV files from a specific folder.
     *
     * @param path path of a CSV job containing information of a set of jobs
     */
    public void loadInstanceDirectory(String path) {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (!listOfFiles[i].isDirectory())
                this.addInstance(new Instance(listOfFiles[i].toString()));
        }
    }

    public void addInstance(Instance instance) {
        this.instances.add(instance);
    }
}
