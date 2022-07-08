package it.uniroma2.dicii.amod.onerjscheduling.solvers;

import it.uniroma2.dicii.amod.onerjscheduling.entities.BnBProblem;
import it.uniroma2.dicii.amod.onerjscheduling.entities.Instance;
import it.uniroma2.dicii.amod.onerjscheduling.entities.output.InstanceExecResult;
import it.uniroma2.dicii.amod.onerjscheduling.entities.Job;
import it.uniroma2.dicii.amod.onerjscheduling.objectfunctions.ObjectFunction;
import it.uniroma2.dicii.amod.onerjscheduling.scheduling.Schedule;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class BnBNonBackwardSolver extends BnBSolver {
    protected boolean checkForExpansion;

    @Override
    public InstanceExecResult solveExecutive(Instant start, ObjectFunction objFn, Instance instance) {

        while (this.openBnBProblems.size() > 0) {
            if (checkTimeout(start))
                return new InstanceExecResult(this.incumbent, this.globLB);
            BnBProblem p = this.openBnBProblems.get(0);
            this.openBnBProblems.remove(p);
            p = examineProblem(p, objFn);
            this.openBnBProblems.addAll(this.generateSubProblems(p, this.checkForExpansion));
          //  this.updateStatuses(p.getStatus());
        }
        return new InstanceExecResult(this.incumbent, this.globLB);
    }

    @Override
    protected List<BnBProblem> generateSubProblems(BnBProblem p, boolean checkForExpansion) {
        List<BnBProblem> ret = new ArrayList<>();
        if (p.isExpandable() || !checkForExpansion) {
            // esclusione del livello delle foglie
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
            p.setExpanded();
        }
        return ret;
    }
}
