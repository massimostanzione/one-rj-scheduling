package it.uniroma2.dicii.amod.onerjscheduling.scheduling;

import it.uniroma2.dicii.amod.onerjscheduling.entities.Job;

import java.util.ArrayList;
import java.util.List;


public class Schedule {
    private List<ScheduleItem> items = new ArrayList<>();

    public List<ScheduleItem> getItems() {
        return items;
    }

    public void setItems(List<ScheduleItem> items) {
        this.items = items;
    }

    public int getCompletionTime() {
        return this.items.size() == 0 ? 0 : this.items.get(this.items.size() - 1).getFinishTime();
    }

    public boolean contains(Job j) {
        for (ScheduleItem i : this.getItems()) {
            if (i.getJob() == j) return true;
        }
        return false;
    }


    @Override
    public String toString() {
        /*String ret = "\nJob\tda\ta\tterminato\n";
        for (ScheduleItem i : items) {
            ret += i.getJob().getId() + "\t" + i.getStartTime() + "\t" + i.getFinishTime() + "\t" + !i.isPreempted() + "\n";
        }
        return ret;*/
        String ret="";
        for (ScheduleItem i : items) {
            ret += i.getJob().getId() + " ";
        }
        return ret;

    }

    public int countElapsedTime(Job j) {
        int ret = 0;
        for (ScheduleItem i : this.getItems()) {
            if (i.getJob() == j)
                ret += i.getFinishTime() - i.getStartTime();
        }
        return ret;
    }


    public boolean isPreempted() {
        for (ScheduleItem i : this.getItems()) {
            if (i.isPreempted()) return true;
        }
        return false;
    }



    public boolean isCompleted(List<Job> jobList) {
        for (Job j : jobList) {
            if (!contains(j) || countElapsedTime(j) < j.getProcessingTime()) {
                //System.out.println("La schedula non è ancora completa.");
                return false;
            }
        }
        return true;
    }
}
