package it.uniroma2.dicii.amod.onerjscheduling.exceptions;

import it.uniroma2.dicii.amod.onerjscheduling.utils.ProblemStatus;

public class ClosedStatusException extends RuntimeException {
    public ClosedStatusException(ProblemStatus oldStatus, ProblemStatus attemptedStatus) {
        super("Attempted to update final status "+oldStatus+" with "+attemptedStatus);

    }
}
