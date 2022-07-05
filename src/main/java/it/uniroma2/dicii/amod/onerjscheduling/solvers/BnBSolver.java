package it.uniroma2.dicii.amod.onerjscheduling.solvers;

import it.uniroma2.dicii.amod.onerjscheduling.dao.DataDAO;
import it.uniroma2.dicii.amod.onerjscheduling.entities.BnBProblem;
import it.uniroma2.dicii.amod.onerjscheduling.entities.Job;
import it.uniroma2.dicii.amod.onerjscheduling.scheduling.Schedule;
import it.uniroma2.dicii.amod.onerjscheduling.utils.ExternalConfig;
import it.uniroma2.dicii.amod.onerjscheduling.utils.ProblemStatus;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static it.uniroma2.dicii.amod.onerjscheduling.utils.ProblemStatus.EXPANDED;

public abstract class BnBSolver extends Solver {
    protected List<Job> jobList;
    protected String prot = "none";
    protected List<BnBProblem> openBnBProblems;
    protected int incumbent;
    protected int globLB;
    protected Map<ProblemStatus, Integer> statuses;
    /*  protected List<BnBProblem> openBnBProblems;
    protected int incumbent;
    protected int globLB;

    public BnBSolver() {
        this.jobList = new ArrayList<>();
        this.openBnBProblems = new ArrayList<>();
        this.incumbent = Integer.MAX_VALUE;
        this.globLB = Integer.MAX_VALUE;
    }*/

    protected static boolean checkDominance(BnBProblem p, List<Job> jobList) {
        // obiettivo: verificare se tra l'ultimo della sequenza pre-esistente
        // e l'ultimo che è stato aggiunto
        // ci entra un altro job
        Schedule s = p.getFullInitialSchedule();
        Job lastAddedJob = p.getLastAddedJob();
        if (lastAddedJob != null) {
            for (Job j : getJobsNotInSchedule(jobList, s)) {
                if (lastAddedJob.getReleaseDate() >= Math.max(j.getReleaseDate(), (p.getInitialSchedule().getCompletionTime())) + j.getProcessingTime()) {
                    //System.out.println("FATHOMED BY DOMINANCE");
                    return true;
                }
            }
        }
        return false;
    }

    private static List<Job> getJobsNotInSchedule(List<Job> jobList, Schedule s) {
        List<Job> ret = new ArrayList<>();
        for (Job j : jobList) {
            if (!s.contains(j)) {
                ret.add(j);
            }
        }
        return ret;
    }

    public void initializeSolverParams() {
        //TODO spostare dalle classi sottostanti
    }

    protected void updateStatuses(ProblemStatus status) {
        if (this.statuses.containsKey(status))
            this.statuses.put(status, this.statuses.get(status) + 1);
        else
            this.statuses.put(status, 1);
    }

    protected BnBProblem processProblem(BnBProblem p) {/*
        this.jobList = new ArrayList<>();
        this.openBnBProblems = new ArrayList<>();
        this.incumbent = Integer.MAX_VALUE;
        this.globLB = Integer.MAX_VALUE;*/
        p.setFinalSchedule(this.objFunction.computeRelaxedSchedule(p.getFullInitialSchedule(), jobList));
        p.setFeasible(!p.getFinalSchedule().isPreempted());
        p.setSolution(this.objFunction.compute(p.getFinalSchedule()));
        return p;
    }

    protected BnBProblem examineProblem(BnBProblem p, BnBProblem rootBnBProblem) {
        p.setStatus(ProblemStatus.PROCESSING);
        if (this.checkDominance(p, this.jobList)) {
            p.setStatus(ProblemStatus.FATHOMED_DOMINANCE);
            p.setSolution(-1);
        } else {
            p = this.processProblem(p);
            int res = p.getSolution();
            if (p == rootBnBProblem) this.globLB = res;
            if (res >= this.incumbent) p.setStatus(ProblemStatus.FATHOMED_BOUNDING);
            if (res < this.incumbent && p.isFeasible()) {
                //System.out.println("******** ammissibilità + ottimalità(soluz. migliore dell'incumbent): AGGIORNO L'INCUMBENT OTTIMO DA " + incumbent + " A " + res);
                this.incumbent = res;
                //il lb globale è mantenuto fermo, questo per il controllo seguente:
                if (this.incumbent == this.globLB) {
                    //System.out.println(">>> FINE: trovato OTTIMO (garantito) per il non-pmnt! AMMISSIBILITÀ e inoltre" + incumbent + " = " + globLB);
                    p.setStatus(ProblemStatus.OPTIMUM_REACHED);
                    //break;
                }
            }
            //p.setSolution(res); la soluzione è già dentro p, settata da processProblem(p);
        }
        //se si è arrivati a questo punto il nodo può essere marcato come espandibile
        if (p.getStatus() == ProblemStatus.PROCESSING) {
            p.setStatus(EXPANDED);
        }
        return p;
    }

    public List<Job> getJobList() {
        return this.jobList;
    }

    public void setJobList(List<Job> jobList) {
        this.jobList = jobList;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
        this.setJobList(DataDAO.getSingletonInstance().getDataInstances(path));
    }

    @Override
    protected void printStats() {
        int sum = 0;
        for (int val : this.statuses.values()) {
            sum += val;
        }
        //long tot = computeNodesNo(this.jobList.size());
        System.out.println(sum + " tree nodes visited");
        System.out.println(statuses);
        System.out.println("\n");
    }

    protected boolean checkTimeout(Instant start) {
        return Duration.between(start, Instant.now()).toMillis() >= ExternalConfig.getSingletonInstance().getComputationTimeout();
    }

    private long computeNodesNo(int size) {
        long sum = size, incr = size;
        for (int i = size - 1; i >= 2; i--) {
            incr = incr * i;
            sum += incr;
        }
        // add root node
        return sum + 1;
    }

    protected List<BnBProblem> generateSubProblems(BnBProblem p, boolean checkForExpansion) {
        List<BnBProblem> ret = new ArrayList<>();
        if (p.getStatus() == EXPANDED || !checkForExpansion) {
            if (p.getFullInitialSchedule().getItems().size() < this.jobList.size() - 1) {
                // prendi la lista dei job, ordinali per id (per convenzione)
                jobList.sort(Comparator.comparing(Job::getId));
                // e aggiungine UNO SOLO alla schedula già presente in p
                // con controllo che non deve già appartenere ad essa
                for (Job j : jobList) {
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
