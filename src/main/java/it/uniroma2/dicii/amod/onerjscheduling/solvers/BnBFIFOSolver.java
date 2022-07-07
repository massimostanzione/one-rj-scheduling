package it.uniroma2.dicii.amod.onerjscheduling.solvers;

import it.uniroma2.dicii.amod.onerjscheduling.control.Scheduler;
import it.uniroma2.dicii.amod.onerjscheduling.entities.BnBProblem;
import it.uniroma2.dicii.amod.onerjscheduling.entities.DataInstance;
import it.uniroma2.dicii.amod.onerjscheduling.entities.ExecutionReportItem;
import it.uniroma2.dicii.amod.onerjscheduling.objectfunctions.ObjectFunction;
import it.uniroma2.dicii.amod.onerjscheduling.scheduling.Schedule;
import it.uniroma2.dicii.amod.onerjscheduling.utils.ProblemStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BnBFIFOSolver extends BnBDFSSolver {
   // private Boolean levelUp;

    @Override
    public ExecutionReportItem solveExecutive(Instant start, ObjectFunction objFn, DataInstance instances) {
        this.openBnBProblems = new ArrayList<>();
        this.incumbent = Integer.MAX_VALUE;
        this.globLB = Integer.MAX_VALUE;
        this.statuses = new HashMap<>();

        Schedule initalSchedule = new Schedule();
        BnBProblem rootBnBProblem = new BnBProblem(initalSchedule);
        this.openBnBProblems.add(rootBnBProblem);
        //this.allNodes.add(rootBnBProblem);

        Scheduler sch = new Scheduler();
        this.potentiallyExpandableInCurrentLevel = new ArrayList<>();
        this.potentiallyExpandableInCurrentLevel.add(rootBnBProblem);
        while (this.openBnBProblems.size() > 0) {
            if (checkTimeout(start))    // TODO sostituibile con break?
                return new ExecutionReportItem(this.incumbent, this.globLB);
            BnBProblem p = this.openBnBProblems.get(0);
            this.openBnBProblems.remove(p);
            // il nodo potrebbe già essere stato visitato se sto risalendo

            if (!p.isVisited()) {
                p = this.examineProblem(p, rootBnBProblem, objFn);
                if (!sch.scheduleInProblemListByFullInitSchedule(p.getFullInitialSchedule(), this.potentiallyExpandableInCurrentLevel))
                    this.potentiallyExpandableInCurrentLevel.add(p);
            }
            List<BnBProblem> sub = this.generateSubProblems(p);
            if (sub != null)
                this.openBnBProblems.addAll(sub);
            //this.allNodes.addAll(sub);
           // this.updateStatuses(p.getStatus());
        }
        return new ExecutionReportItem(this.incumbent, this.globLB);
    }
/*
    private BnBProblem generateNextProblemFIFO(BnBProblem p) {
        Scheduler sch = new Scheduler();
        int skip = 0;
        this.jobList.sort(Comparator.comparing(Job::getId));
        BnBProblem next = null;
        Boolean goBack = null;
        // se non è una foglia ho ancora possibilità di scendere, e vedo se posso farlo:
        if (!isLeaf(p)) {
            // se il problema è EXPANDED vuol dire che già l'ho visitato,
            // e quindi posso ancora provare eventualmente a scendere,
            // mentre se già so che è FATHOMED è inutile che ci provo, devo per forza salire
            if (p.getStatus() == ProblemStatus.EXPANDED) {
                // genero la sequenza
                Schedule s = new Schedule();
                for (Job j : this.jobList) {
                    this.levelUp = null;
                    if (!p.getFullInitialSchedule().contains(j)) {
                        s.getItems().removeAll(s.getItems());
                        s.getItems().addAll(p.getFullInitialSchedule().getItems());
                        next = new BnBProblem(s, j);
                        // se la schedula che ho trovato non è stata ancora esaminata, allora va bene
                        if (!sch.scheduleInProblemListByFullInitSchedule(next.getFullInitialSchedule(), this.potentiallyExpandable)) {
                            //  scendo di figlio
                            this.levelUp = false;
                            return next;
                        }
                    }
                }
                // se sono qui vuol dire che NON ESISTONO in questo livello schedule
                // che non siano già state esaminate,
                // quindi devo salire di livello
                this.levelUp = true;
            }
            // il problema era non-EXPANDED, quindi non potevo andare più a fondo,
            // quindi devo salire
        }
        // il problema era FOGLIA, quindi non potevo andare più a fondo,
        // quindi devo salire

        // QUINDI: se sono qui vuol dire che devo per forza salire
        // vado a vedere nella lista dei visitati, a partire dal più recente a tornare indietro
        for (int i = this.potentiallyExpandable.size() - 1; i >= 0; i--) {
            // se è EXPANDED ed è del livello superiore
            if (this.potentiallyExpandable.get(i).getStatus() == ProblemStatus.EXPANDED
                    && isSuperiorLevel(this.potentiallyExpandable.get(i), p)) {
                this.levelUp = true;
                return this.potentiallyExpandable.get(i);
            }
        }
        // se arrivo qui non ho più nulla da esaminare
        return null;
    }

    /**
     * return true if a is at a superior level wrt b
     *
     * @param a
     * @param b
     * @return
     *//*
    private boolean isSuperiorLevel(BnBProblem a, BnBProblem b) {
        return a.getFullInitialSchedule().getItems().size() <=
                b.getFullInitialSchedule().getItems().size() - 1;
    }

    private boolean isLeaf(BnBProblem p) {
        return p.getFullInitialSchedule().getItems().size() == this.jobList.size() - 1;
    }*/

    @Override
    public SolverEnum initName() {
        return SolverEnum.BRANCH_AND_BOUND_FIFO;
    }
}
