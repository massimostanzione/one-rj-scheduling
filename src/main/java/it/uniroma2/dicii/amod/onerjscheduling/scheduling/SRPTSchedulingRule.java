package it.uniroma2.dicii.amod.onerjscheduling.scheduling;

import it.uniroma2.dicii.amod.onerjscheduling.control.Scheduler;
import it.uniroma2.dicii.amod.onerjscheduling.entities.Job;
import it.uniroma2.dicii.amod.onerjscheduling.exceptions.InvalidTimeException;
import it.uniroma2.dicii.amod.onerjscheduling.utils.SchedulingRuleEnum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * SRPT (Shortest Remaining Time First) scheduling rule.
 * It is assigned to the <code>TotalCompletionTime</code> objective function.
 */
public class SRPTSchedulingRule extends SchedulingRule {

    @Override
    public void initName() {
        this.name = SchedulingRuleEnum.RULE_SRPT;
    }

    @Override
    public Schedule execute(Schedule initialSchedule, List<Job> jobList) {
        Scheduler sch = new Scheduler();
        int clock = initialSchedule.getCompletionTime();
        Job next = null;
        Schedule schedule = new Schedule();
        schedule.getItems().addAll(initialSchedule.getItems());
        List<Integer> events = new ArrayList<>();
        // fill the event arrays with:
        // - release times
        // - completion times:
        // -- jobs that will be scheduled after fixed schedule: see next block
        // -- jobs that already are into the fixed schedule: processing now
        for (Job j : jobList) {
            // ignoring all that is happening during the fixed schedule, moving the clock forward
            if (j.getReleaseDate() > schedule.getCompletionTime()
                    && !events.contains(j.getReleaseDate())) {
                events.add(j.getReleaseDate());
            }
        }
        // (from above)
        // -- jobs that already are into the fixed schedule:
        // --- we only need completion time of the subsequence,
        //     ignoring whatever ends in the meanwhile
        if (!events.contains(schedule.getCompletionTime())) {
            events.add(schedule.getCompletionTime());
        }
        Collections.sort(events);
        int eventIndex = 0;
        do {
            clock = events.get(eventIndex);
            next = pickJob(jobList, clock, schedule);
            // could have idle times
            if (next != null) {
                // next-to-schedule is ready to schedule
                schedule = sch.schedulePmnt(schedule, clock, next, events);
                ScheduleItem item = schedule.getItems().get(schedule.getItems().size() - 1);
                // update events clock
                if (!events.contains(item.getFinishTime())) {
                    events.add(item.getFinishTime());
                    Collections.sort(events);
                }
            }
            eventIndex++;
        }
        while (!schedule.isCompleted(jobList));
        return schedule;
    }

    /**
     * Select next job to schedule
     *
     * @param jobList  job list where look into for the next job
     * @param clock    time instant for scheduling
     * @param schedule schedule already present
     * @return next job to schedule
     */
    private Job pickJob(List<Job> jobList, int clock, Schedule schedule) {
        Job next = null;
        List<Job> releasedJobs = getReleasedJobs(jobList, clock);
        int srpt = -1;
        int minSrpt = Integer.MAX_VALUE;
        for (Job j : releasedJobs) {
            if (!schedule.contains(j) || (schedule.contains(j) && computeRemainingTime(j, schedule) > 0)) {
                srpt = computeRemainingTime(j, schedule);
                if (srpt < minSrpt) {
                    // mark it as eligible for scheduling
                    next = j;
                    minSrpt = srpt;
                }
            }
        }
        return next;
    }

    private List<Job> getReleasedJobs(List<Job> jobList, Integer clock) {
        List<Job> ret = new ArrayList<>();
        for (Job j : jobList) {
            if (j.getReleaseDate() <= clock)
                ret.add(j);
        }
        ret.sort(Comparator.comparing(Job::getReleaseDate));
        return ret;
    }

    private int computeRemainingTime(Job j, Schedule s) {
        int ret = j.getProcessingTime();
        for (ScheduleItem i : s.getItems()) {
            if (i.getJob() == j) {
                ret -= (i.getFinishTime() - i.getStartTime());
            }
        }
        if (ret < 0) {
            throw new InvalidTimeException("Negative remaining time, ret = " + ret);
        }
        return ret;
    }
}
