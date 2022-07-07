package it.uniroma2.dicii.amod.onerjscheduling.solvers;

import it.uniroma2.dicii.amod.onerjscheduling.control.Scheduler;
import it.uniroma2.dicii.amod.onerjscheduling.entities.BnBProblem;
import it.uniroma2.dicii.amod.onerjscheduling.entities.Job;
import it.uniroma2.dicii.amod.onerjscheduling.scheduling.Schedule;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class BnBDFSSolver extends BnBSolver {
    protected List<BnBProblem> potentiallyExpandableInCurrentLevel;
    protected Boolean levelUp;


    protected List<BnBProblem> generateSubProblems(BnBProblem p) {
        return this.generateSubProblems(p, true);
    }

    @Override
    protected List<BnBProblem> generateSubProblems(BnBProblem p, boolean checkForExpansion) {
        //System.out.println("cerco di generare sottoproblemi per "+p);
        List<BnBProblem> ret = new ArrayList<>();
        Scheduler sch = new Scheduler();
        int skip = 0;
        this.jobList.sort(Comparator.comparing(Job::getId));
        BnBProblem next = null;
        Boolean goBack = null;
        // se non è una foglia ho ancora possibilità di scendere, e vedo se posso farlo:
        if (!isLeaf(p)) {
            // se il problema è EXPANDABLE vuol dire che già l'ho visitato,
            // e quindi posso ancora provare eventualmente a scendere,
            // mentre se già so che è FATHOMED è inutile che ci provo, devo per forza salire
            if (p.isWaitingForFurtherProcessing()) {
                // genero la sequenza
                Schedule s = new Schedule();
                for (Job j : this.jobList) {
                    this.levelUp = null;
                    if (!p.getFullInitialSchedule().contains(j)) {
                        s.getItems().removeAll(s.getItems());
                        s.getItems().addAll(p.getFullInitialSchedule().getItems());
                        next = new BnBProblem(s, j);
                        // se la schedula che ho trovato non è stata ancora esaminata, allora va bene
                        if (!sch.scheduleInProblemListByFullInitSchedule(next.getFullInitialSchedule(), this.potentiallyExpandableInCurrentLevel)) {
                            //  scendo di figlio
                            this.levelUp = false;
                            //return next;
                            //System.out.println("Nuovo sottoproblema: "+next);
                            ret.add(next);
                        }
                    }
                }
                // se ho dei sottoproblemi generati vuol dire che avevo ancora schedule da esplorare
                // e le ho aggiunte a ret; non posso generarne di ulteriori, quindi ritorno
                if (ret.size() > 0) {
                    p.setExpanded();
                    //System.out.println("Ho espanso: "+p);
                    return ret;
                }
                // se invece sono qui vuol dire che NON ESISTONO in questo livello schedule
                // che non siano già state esaminate,
                // quindi devo salire di livello
                this.levelUp = true;    // TODO ma serve?
            }
            // il problema era non-EXPANDABLE, quindi non potevo andare più a fondo,
            // quindi devo salire
        }
        // il problema era FOGLIA, quindi non potevo andare più a fondo,
        // quindi devo salire

        // QUINDI: se sono qui vuol dire che devo per forza salire
        // vado a vedere nella lista dei visitati, a partire dal più recente a tornare indietro
        for (int i = this.potentiallyExpandableInCurrentLevel.size() - 1; i >= 0; i--) {
            // se è EXPANDABLE ed è del livello superiore
            if (this.potentiallyExpandableInCurrentLevel.get(i).isWaitingForFurtherProcessing()
                    && isSuperiorLevel(this.potentiallyExpandableInCurrentLevel.get(i), p)) {
                this.levelUp = true;
                ret.add(this.potentiallyExpandableInCurrentLevel.get(i));
                //return this.treeBranchProblems.get(i);
            }
        }
        // se arrivo qui non ho più nulla da esaminare
        p.setExpanded();
        return ret;
    }

    /**
     * return true if a is at a superior level wrt b
     *
     * @param a
     * @param b
     * @return
     */
    private boolean isSuperiorLevel(BnBProblem a, BnBProblem b) {
        return a.getFullInitialSchedule().getItems().size() <=
                b.getFullInitialSchedule().getItems().size() - 1;
    }

    private boolean isLeaf(BnBProblem p) {
        return p.getFullInitialSchedule().getItems().size() == this.jobList.size() - 1;
    }
}
