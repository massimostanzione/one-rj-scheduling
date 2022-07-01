package it.uniroma2.dicii.amod.onerjscheduling.entities;

import it.uniroma2.dicii.amod.onerjscheduling.control.Scheduler;
import it.uniroma2.dicii.amod.onerjscheduling.scheduling.Schedule;
import it.uniroma2.dicii.amod.onerjscheduling.utils.ProblemStatus;

import static it.uniroma2.dicii.amod.onerjscheduling.utils.ProblemStatus.NOT_VISITED;

public class BnBProblem {

    private Boolean feasible = null;
    private Schedule initialSchedule = null;
    private Job lastAddedJob = null;
    private Schedule finalSchedule = null;
    private ProblemStatus status;
    private Integer solution = null;

    public BnBProblem(Schedule initalSchedule) {
        this.initialSchedule = initalSchedule;
        this.status = NOT_VISITED;
    }

    public BnBProblem(Schedule s, Job j) {
        this(s);
        this.lastAddedJob = j;
    }

    public Integer getSolution() {
        return solution;
    }

    public void setSolution(Integer solution) {
        this.solution = solution;
    }

    @Override
    public String toString() {
        return "BnBProblem{" +
                "feasible=" + feasible +
                ", initialSchedule=" + initialSchedule +
                ", lastAddedJob=" + lastAddedJob +
                ", finalSchedule=" + finalSchedule +
                ", status=" + status +
                ", solution=" + solution +
                '}';
    }

    public void setFeasible(Boolean feasible) {
        this.feasible = feasible;
    }

    public ProblemStatus getStatus() {
        return status;
    }

    public void setStatus(ProblemStatus status) {
        this.status = status;
    }

    public Schedule getInitialSchedule() {
        return initialSchedule;
    }

    public Schedule getFullInitialSchedule() {
        Scheduler sch = new Scheduler();//TODO singleton?
        Schedule full = new Schedule();
        full.getItems().addAll(initialSchedule.getItems());
        if (lastAddedJob != null) {
            full = sch.scheduleNonPmnt(full, lastAddedJob);
        }
        return full;
    }

    public Schedule getFinalSchedule() {
        return finalSchedule;
    }

    public void setFinalSchedule(Schedule finalSchedule) {
        this.finalSchedule = finalSchedule;
    }

    public Boolean isFeasible() {
        return feasible;
    }

    public Job getLastAddedJob() {
        return lastAddedJob;
    }

    public void setLastAddedJob(Job lastAddedJob) {
        this.lastAddedJob = lastAddedJob;
    }
}
