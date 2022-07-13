package it.uniroma2.dicii.amod.onerjscheduling.scheduling;

import it.uniroma2.dicii.amod.onerjscheduling.entities.Job;

import java.util.ArrayList;
import java.util.List;

/**
 * This class models a schedule for a set of jobs.
 */
public class Schedule {
    private List<ScheduleItem> items = new ArrayList<>();

    public int getCompletionTime() {
        return this.items.size() == 0 ? 0 : this.items.get(this.items.size() - 1).getFinishTime();
    }

    /**
     * Just a <code>toString()</code> with more detailed output for the schedule
     *
     * @return
     */
    public String toDetailedString() {
        String ret = "\nJob\tfrom\tto\tterminated\n";
        for (ScheduleItem i : items) {
            ret += i.getJob().getId() + "\t" + i.getStartTime() + "\t" + i.getFinishTime() + "\t" + !i.isPreempted() + "\n";
        }
        return ret;
    }

    public int size() {
        return this.items.size();
    }

    public boolean isPreempted() {
        for (ScheduleItem i : this.getItems()) {
            if (i.isPreempted()) return true;
        }
        return false;
    }

    public List<ScheduleItem> getItems() {
        return items;
    }

    public void setItems(List<ScheduleItem> items) {
        this.items = items;
    }

    public boolean isCompleted(List<Job> jobList) {
        for (Job j : jobList) {
            if (!contains(j) || countElapsedTime(j) < j.getProcessingTime()) {
                return false;
            }
        }
        return true;
    }

    public boolean contains(Job j) {
        for (ScheduleItem i : this.getItems()) {
            if (i.getJob() == j) return true;
        }
        return false;
    }

    public int countElapsedTime(Job j) {
        int ret = 0;
        for (ScheduleItem i : this.getItems()) {
            if (i.getJob() == j)
                ret += i.getFinishTime() - i.getStartTime();
        }
        return ret;
    }

    @Override
    public String toString() {
        String ret = "";
        for (ScheduleItem i : items) {
            ret += i.getJob().getId() + " ";
        }
        return ret;

    }
}
