package it.uniroma2.dicii.amod.onerjscheduling.scheduling;

import it.uniroma2.dicii.amod.onerjscheduling.control.Scheduler;
import it.uniroma2.dicii.amod.onerjscheduling.entities.Job;
import it.uniroma2.dicii.amod.onerjscheduling.utils.SchedulingRuleEnum;

import java.util.Comparator;
import java.util.List;

/**
 * Just a simple example of how a scheduling rule works in this program:
 * just sort jobs by non-decreasing release date
 * and schedule them in a non-preemtive way, as they are ordered.
 */
public class DummySchedulingRule extends SchedulingRule {

    @Override
    public void initName() {
        this.name = SchedulingRuleEnum.DUMMY;
    }

    @Override
    public Schedule execute(Schedule s, List<Job> jobList) {
        Scheduler sch = new Scheduler();
        Schedule schedule = new Schedule();
        jobList.sort(Comparator.comparing(Job::getReleaseDate));
        for (Job j : jobList) {
            sch.scheduleNonPmnt(s, j);
        }
        return schedule;
    }
}
