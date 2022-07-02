package it.uniroma2.dicii.amod.onerjscheduling.scheduling;

import it.uniroma2.dicii.amod.onerjscheduling.control.Scheduler;
import it.uniroma2.dicii.amod.onerjscheduling.entities.Job;
import it.uniroma2.dicii.amod.onerjscheduling.utils.SchedulingRuleEnum;

import java.util.Comparator;
import java.util.List;

public class DummySchedulingRule extends SchedulingRule {

    @Override
    public void initName() {
    }
    @Override
    public Schedule execute(Schedule s, List<Job> jobList) {
        Scheduler sch = new Scheduler();//TODO singleton?
        Schedule schedule = new Schedule();
        // ordina i job per data di rilascio non decrescente
        jobList.sort(Comparator.comparing(Job::getReleaseDate));
        // schedula in modo non-pmnt uno di seguito all'altro
        for (Job j : jobList) {
            sch.scheduleNonPmnt(s, j);
        }
        return schedule;
    }
}
