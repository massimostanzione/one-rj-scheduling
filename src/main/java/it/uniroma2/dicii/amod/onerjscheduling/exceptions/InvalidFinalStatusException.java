package it.uniroma2.dicii.amod.onerjscheduling.exceptions;

import it.uniroma2.dicii.amod.onerjscheduling.entities.BnBProblem;

public class InvalidFinalStatusException extends RuntimeException {
    public InvalidFinalStatusException(BnBProblem p) {
        super("Node not closed at the end of the computation.\t" +
                "\tNode:\t" + p + "\n\tStatus:\t" + p.getStatus());
    }
}
