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
    public Schedule scheduleNonPmnt(Schedule s, Job j) {
        // semplicemente schedula dopo l'ultimo job che è già nella schedula
        ScheduleItem newItem = new ScheduleItem();
        newItem.setJob(j);
        // ottieni l'istante di completamento dell'ultimo item in lista
        int startTime = s.getCompletionTime() > j.getReleaseDate() ? s.getCompletionTime() : j.getReleaseDate();
        newItem.setStartTime(startTime);

        // indi schedula il nuovo item:
        // parte all'istante appena ottenuto
        // termina all'istante ottenuto + il tempo di completamento
        newItem.setFinishTime(newItem.getStartTime() + j.getProcessingTime());
        newItem.setPreempted(false);
        appendItem(s, newItem);
        return s;
    }

    public Schedule schedulePmnt(Schedule s, int clock, Job next, List<Job> jobList, List<Integer> events) {
        ScheduleItem newItem = new ScheduleItem();
        // vedo prima se è già stato schedulato
        // (per decidere il remaining time, quanto tempo deve occupare)
        int remTime = next.getProcessingTime() - s.countElapsedTime(next);
        // clock: dove sono temporalmente?
        // from è l'ultimo + 1 (ovvero clock + 0)
        // ma to è fin dove può arrivare prima della prossima decisione
        // quindi devo vedere quando avviene il prossimo evento (release date)
        int nextEventInst = -1;
        for (Integer i : events) {
            if (i > clock && i <= clock + remTime) {
                // se l'evento successivo è ancora all'interno del processamento del job
                // considera la schedula fino a quel momento,
                // al resto ci si pensa dopo, non sappiamo se continuerà lui oppure no
                nextEventInst = i;
                break;
            }
            nextEventInst = clock + remTime;
        }
        boolean pmnt = nextEventInst < (clock + remTime);

        if (s.getItems().size() > 0 && next == s.getItems().get(s.getItems().size() - 1).getJob()) {
            s.getItems().get(s.getItems().size() - 1).setFinishTime(nextEventInst);
            s.getItems().get(s.getItems().size() - 1).setPreempted(pmnt);
            // System.out.println("*** ESTENDO GIÀ SCHEDULATO: job " + this.items.get(this.items.size() - 1).getJob().getId() + " fino a " + nextEventInst);

            return s;   //TODO unire i due return
        } else {
            newItem.setJob(next);
            newItem.setStartTime(clock);
            newItem.setFinishTime(nextEventInst);
            newItem.setPreempted(pmnt);
            appendItem(s, newItem);
            return s;
        }
    }

    private void appendItem(Schedule s, ScheduleItem item) {
        s.getItems().add(item);
    }

    public boolean areEqualByJobs(Schedule s1, Schedule s2) {
        if (s1.getItems().size() != s2.getItems().size()) return false;
        for (int i = 0; i < s1.getItems().size(); i++) {
            if (s1.getItems().get(i).getJob().getId() != s2.getItems().get(i).getJob().getId()) {
                return false;
            }
        }
        return true;
    }

    public boolean scheduleInProblemListByFullInitSchedule(Schedule s, List<BnBProblem> list) {
        for (BnBProblem p : list) {
            if (areEqualByJobs(p.getFullInitialSchedule(), s)) return true;
        }
        return false;
    }
}
