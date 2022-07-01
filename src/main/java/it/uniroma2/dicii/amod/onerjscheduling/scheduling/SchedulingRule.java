package it.uniroma2.dicii.amod.onerjscheduling.scheduling;

import it.uniroma2.dicii.amod.onerjscheduling.entities.Job;

import java.util.List;
//TODO classe astratta?
public interface SchedulingRule {
    public Schedule execute(Schedule initialSchedule, List<Job> jobList);
    //Job pickJob(List<Job> jobList, int clock, Schedule schedule);
}
