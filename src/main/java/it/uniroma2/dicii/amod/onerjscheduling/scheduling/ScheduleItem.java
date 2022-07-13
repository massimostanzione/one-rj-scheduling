package it.uniroma2.dicii.amod.onerjscheduling.scheduling;

import it.uniroma2.dicii.amod.onerjscheduling.entities.Job;

/**
 * This class contains information about a single job execution, wheter it is partial or complete.
 */
public class ScheduleItem {
    private Job j;
    private int startTime;
    private int finishTime;
    private Boolean preempted = null;

    public Job getJob() {
        return j;
    }

    public void setJob(Job j) {
        this.j = j;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    public Boolean isPreempted() {
        return preempted;
    }

    public void setPreempted(Boolean preempted) {
        this.preempted = preempted;
    }
}
