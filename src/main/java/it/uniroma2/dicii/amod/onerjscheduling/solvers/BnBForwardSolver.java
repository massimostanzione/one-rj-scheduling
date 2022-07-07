package it.uniroma2.dicii.amod.onerjscheduling.solvers;

import it.uniroma2.dicii.amod.onerjscheduling.entities.BnBProblem;
import it.uniroma2.dicii.amod.onerjscheduling.entities.Job;
import it.uniroma2.dicii.amod.onerjscheduling.scheduling.Schedule;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static it.uniroma2.dicii.amod.onerjscheduling.utils.ProblemStatus.EXPANDED;

public class BnBForwardSolver extends BnBNonBackwardSolver {

    public BnBForwardSolver() {
        this.checkForExpansion=true;
    }

    @Override
    public SolverEnum initName() {
        return SolverEnum.BRANCH_AND_BOUND_FORWARD;
    }

}
