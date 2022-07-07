package it.uniroma2.dicii.amod.onerjscheduling.solvers;

import it.uniroma2.dicii.amod.onerjscheduling.entities.BnBProblem;
import it.uniroma2.dicii.amod.onerjscheduling.entities.DataInstance;
import it.uniroma2.dicii.amod.onerjscheduling.entities.ExecutionReportItem;
import it.uniroma2.dicii.amod.onerjscheduling.entities.Job;
import it.uniroma2.dicii.amod.onerjscheduling.objectfunctions.ObjectFunction;
import it.uniroma2.dicii.amod.onerjscheduling.scheduling.Schedule;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static it.uniroma2.dicii.amod.onerjscheduling.utils.ProblemStatus.EXPANDED;

public abstract class BnBNonBackwardSolver extends BnBSolver {
    protected boolean checkForExpansion;

    @Override
    public ExecutionReportItem solveExecutive(Instant start, ObjectFunction objFn, DataInstance instance) {
        this.openBnBProblems = new ArrayList<>();
        this.incumbent = Integer.MAX_VALUE;
        this.globLB = Integer.MAX_VALUE;
        this.statuses = new HashMap<>();

        Schedule initalSchedule = new Schedule();
        BnBProblem rootBnBProblem = new BnBProblem(initalSchedule);
        this.openBnBProblems.add(rootBnBProblem);

        while (this.openBnBProblems.size() > 0) {
            if (checkTimeout(start))
                return new ExecutionReportItem(this.incumbent, this.globLB);
            BnBProblem p = this.openBnBProblems.get(0);
            this.openBnBProblems.remove(p);
            p = examineProblem(p, objFn);
            this.openBnBProblems.addAll(this.generateSubProblems(p, this.checkForExpansion));
          //  this.updateStatuses(p.getStatus());
        }
        return new ExecutionReportItem(this.incumbent, this.globLB);
    }

    @Override
    protected List<BnBProblem> generateSubProblems(BnBProblem p, boolean checkForExpansion) {
        List<BnBProblem> ret = new ArrayList<>();
        if (p.getStatus() == EXPANDED || !checkForExpansion) {
            if (p.getFullInitialSchedule().getItems().size() < this.jobList.size() - 1) {
                // prendi la lista dei job, ordinali per id (per convenzione)
                this.jobList.sort(Comparator.comparing(Job::getId));
                // e aggiungine UNO SOLO alla schedula già presente in p
                // con controllo che non deve già appartenere ad essa
                for (Job j : this.jobList) {
                    //System.out.println("controllo nella seguente schedula:\n"+p.getInitialSchedule());
                    if (!p.getFullInitialSchedule().contains(j)) {
                        Schedule s = new Schedule();
                        s.getItems().addAll(p.getFullInitialSchedule().getItems());
                        BnBProblem child = new BnBProblem(s, j);
                        ret.add(child);
                    }
                }
            }
        }
        return ret;
    }
}
