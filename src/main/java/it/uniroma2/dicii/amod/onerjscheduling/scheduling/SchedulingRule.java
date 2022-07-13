package it.uniroma2.dicii.amod.onerjscheduling.scheduling;

import it.uniroma2.dicii.amod.onerjscheduling.entities.Job;
import it.uniroma2.dicii.amod.onerjscheduling.utils.SchedulingRuleEnum;

import java.util.List;

/**
 * A scheduling rule, to be assigned to a <code>ObjectiveFunction</code> class in order to process
 * the related relaxed <i>branch-and-bound</i> problems.
 */
public abstract class SchedulingRule {
    protected SchedulingRuleEnum name;

    protected abstract void initName();

    public abstract Schedule execute(Schedule initialSchedule, List<Job> jobList);
}
