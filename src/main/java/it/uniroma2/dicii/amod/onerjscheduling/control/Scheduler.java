package it.uniroma2.dicii.amod.onerjscheduling.control;

import it.uniroma2.dicii.amod.onerjscheduling.entities.BnBProblem;
import it.uniroma2.dicii.amod.onerjscheduling.entities.Job;
import it.uniroma2.dicii.amod.onerjscheduling.scheduling.Schedule;
import it.uniroma2.dicii.amod.onerjscheduling.scheduling.ScheduleItem;

import java.util.List;

/**
 * Utility class handling specific operation involving <code>Schedule</code> and <code>ScheduleItem</code> objects.
 */
public class Scheduler {

    /**
     * Schedule the job <code>next</code> in a non-preemptive way in the schedule <code>s</code>.
     *
     * @param s    schedule
     * @param next job to be scheduled
     * @return the new schedule, with <code>next</code> scheduled in a non-preemptive way
     */
    public Schedule scheduleNonPmnt(Schedule s, Job next) {
        ScheduleItem newItem = new ScheduleItem();
        newItem.setJob(next);
        int startTime = Math.max(s.getCompletionTime(), next.getReleaseDate());
        newItem.setStartTime(startTime);
        newItem.setFinishTime(newItem.getStartTime() + next.getProcessingTime());
        newItem.setPreempted(false);
        appendItem(s, newItem);
        return s;
    }

    /**
     * Append the <code>ScheduleItem</code> <code>item</code> to the schedule <code>s</code>.
     *
     * @param s    schedule
     * @param item item to be appended
     */
    private void appendItem(Schedule s, ScheduleItem item) {
        s.getItems().add(item);
    }

    /**
     * Schedule the job <code>next</code> in a preemptive way in the schedule <code>s</code>.
     *
     * @param s      schedule
     * @param clock  current time instant
     * @param next   job to be scheduled
     * @param events event clock array
     * @return the new schedule, with <code>next</code> scheduled in a preemptive way
     */
    public Schedule schedulePmnt(Schedule s, int clock, Job next, List<Integer> events) {
        ScheduleItem newItem = new ScheduleItem();
        int remTime = next.getProcessingTime() - s.countElapsedTime(next);
        int nextEventInst = -1;
        for (Integer i : events) {
            if (i > clock && i <= clock + remTime) {
                nextEventInst = i;
                break;
            }
            nextEventInst = clock + remTime;
        }
        boolean pmnt = nextEventInst < (clock + remTime);
        if (s.getItems().size() > 0 && next == s.getItems().get(s.getItems().size() - 1).getJob()) {
            // Extend and already-scheduled job
            s.getItems().get(s.getItems().size() - 1).setFinishTime(nextEventInst);
            s.getItems().get(s.getItems().size() - 1).setPreempted(pmnt);
        } else {
            newItem.setJob(next);
            newItem.setStartTime(clock);
            newItem.setFinishTime(nextEventInst);
            newItem.setPreempted(pmnt);
            appendItem(s, newItem);
        }
        return s;
    }

    /**
     * Check if schedule <code>s</code> is already present in a <code>fullInitialSchedule</code>
     * in (at least) one of the problems in <code>list</code>.
     *
     * @param s    schedule to be looked for
     * @param list list of BnB problems
     * @return <code>true</code> if schedule is found; <code>false</code> elsewhere.
     */
    public boolean scheduleIsInProblemListByFullInitSchedule(Schedule s, List<BnBProblem> list) {
        for (BnBProblem p : list) {
            if (areEqualByJobs(p.getFullInitialSchedule(), s)) return true;
        }
        return false;
    }

    /**
     * Check whether two schedule are "internally" equal.
     *
     * @param s1 schedule to be compared
     * @param s2 schedule to be compared
     * @return <code>true</code> if the two schedule are equal "by jobs"; <code>false</code> elsewhere.
     */
    public boolean areEqualByJobs(Schedule s1, Schedule s2) {
        if (s1.getItems().size() != s2.getItems().size()) return false;
        for (int i = 0; i < s1.getItems().size(); i++) {
            if (s1.getItems().get(i).getJob().getId() != s2.getItems().get(i).getJob().getId()) {
                return false;
            }
        }
        return true;
    }
}
