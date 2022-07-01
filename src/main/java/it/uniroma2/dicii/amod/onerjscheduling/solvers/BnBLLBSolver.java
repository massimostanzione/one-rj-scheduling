package it.uniroma2.dicii.amod.onerjscheduling.solvers;

import it.uniroma2.dicii.amod.onerjscheduling.entities.BnBProblem;
import it.uniroma2.dicii.amod.onerjscheduling.scheduling.Schedule;
import it.uniroma2.dicii.amod.onerjscheduling.utils.ProblemStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static it.uniroma2.dicii.amod.onerjscheduling.utils.ProblemStatus.EXPANDED;

//TODO gerarchia BnB
public class BnBLLBSolver extends BnBSolver {

    @Override
    public int solveExecutive(Instant start) {
        this.openBnBProblems = new ArrayList<>();
        this.incumbent = Integer.MAX_VALUE;
        this.globLB = Integer.MAX_VALUE;
        this.statuses = new HashMap<>();

        Schedule initalSchedule = new Schedule();
        BnBProblem rootBnBProblem = new BnBProblem(initalSchedule);
        this.openBnBProblems.add(rootBnBProblem);

        List<BnBProblem> potentiallyExpandable = new ArrayList<>();
        potentiallyExpandable.add(rootBnBProblem);

        // QUI GUARDO LA LISTA COME LIVELLO
        while (this.openBnBProblems.size() > 0) {
            if (checkTimeout(start)) return -1;
            Integer min = Integer.MAX_VALUE;
            int res = -1;
            for (BnBProblem p : this.openBnBProblems) {
                p = this.examineProblem(p, rootBnBProblem);
                res = p.getSolution();
                //ora posso decidere la logica di espansione
                if (p.getStatus() == EXPANDED) {
                    if (res < min && res != -1) min = res;
                    if (!potentiallyExpandable.contains(p))
                        potentiallyExpandable.add(p);
                }
            }

            // aggiorno il livello
            this.openBnBProblems.removeAll(this.openBnBProblems);
            //System.out.println("Min = "+min);
            for (BnBProblem p : potentiallyExpandable) {
                if ((p.getSolution().equals(min) && p.getStatus() == EXPANDED) || p == rootBnBProblem) {
                    this.openBnBProblems.addAll(this.generateSubProblems(p, true));
                } else {
                    p.setStatus(ProblemStatus.FATHOMED_BOUNDING);
                }
                this.updateStatuses(p.getStatus());
            }
            potentiallyExpandable.removeAll(potentiallyExpandable);
        }
        return this.incumbent;
    }

    @Override
    public void setName() {
        this.name = SolverEnum.BRANCH_AND_BOUND_LLB;
    }
}
