package it.uniroma2.dicii.amod.onerjscheduling.scheduling;

import it.uniroma2.dicii.amod.onerjscheduling.control.Scheduler;
import it.uniroma2.dicii.amod.onerjscheduling.entities.Job;
import it.uniroma2.dicii.amod.onerjscheduling.objectfunctions.ObjectFunctionEnum;
import it.uniroma2.dicii.amod.onerjscheduling.utils.SchedulingRuleEnum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SRPTSchedulingRule extends SchedulingRule {

    @Override
    public void initName() {
        this.name = SchedulingRuleEnum.RULE_SRPT;
    }
    @Override
    public Schedule execute(Schedule initialSchedule, List<Job> jobList) {
        Scheduler sch = new Scheduler();//TODO singleton?
        int clock = initialSchedule.getCompletionTime();//initialSchedule.getItems().get(initialSchedule.getItems().size()-1).getFinishTime();//TODO in sovrastante?
        Job next = null;
        Schedule schedule = new Schedule();
        schedule.getItems().addAll(initialSchedule.getItems());
        List<Integer> events = new ArrayList<>();
        // riempio l'array degli eventi:
        // - istanti di rilascio (release time)
        // - istanti di completamento:
        // -- per i job che vengono man mano schedulati lo faccio più avanti
        // -- per i job che sono nella schedula FISSA: dopo
        for (Job j : jobList) {
            // ignoro tutto ciò che succede durante la schedula fissa
            if (j.getReleaseDate() >= schedule.getCompletionTime()) {
                events.add(j.getReleaseDate());
            }
        }
        // (segue)
        // -- per i job che sono nella schedula FISSA:
        // --- MI SERVE SOLO IL COMPLETION TIME DELLA SOTTOSEQUENZA,
        //     tutto ciò che termina nel mezzo non mi interessa
        if (!events.contains(schedule.getCompletionTime())) {
            events.add(schedule.getCompletionTime());
        }
        Collections.sort(events);
        int eventIndex = 0;
        do {
            clock = events.get(eventIndex);
            //TODO ev. astrarre secondo regola
            next = pickJob(jobList, clock, schedule);
            // potrei avere idle
            if (next != null) {
                // ora che ho il prossimo da schedulare, lo faccio:
                schedule = sch.schedulePmnt(schedule, clock, next, jobList, events);
                ScheduleItem item = schedule.getItems().get(schedule.getItems().size() - 1);
                // aggiorno l'array degli eventi con l'istante di completamento
                // del processo appena schedulato
                // (la prossima esecuzione avrà clock pari ad esso)
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

    private Job pickJob(List<Job> jobList, int clock, Schedule schedule) {
        Job next = null;
        List<Job> releasedJobs = getReleasedJobs(jobList, clock);
        //System.out.println("In questo momento (" + clock + ") i jobs rilasciati sono: " + releasedJobs);
        int srpt = -1;
        int minSrpt = Integer.MAX_VALUE;
        for (Job j : releasedJobs) {
            if (!schedule.contains(j) || (schedule.contains(j) && computeRemainingTime(j, schedule) > 0)) {
                srpt = computeRemainingTime(j, schedule);
                if (srpt < minSrpt) {
                    // lo aggiorno come possibile candidato
                    next = j;
                    minSrpt = srpt;
                }
            }
        }
        return next;
    }

    private int getNextEventIndex(Integer clock, List<Integer> events) {
        int index = -1;
        for (Integer event : events) {
            index++;
            if (event == clock) return index + 1;
        }
        return index;
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
            throw new RuntimeException("Negative remaining time, ret = " + ret);
        }
        return ret;
    }
}
