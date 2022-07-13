package it.uniroma2.dicii.amod.onerjscheduling.exceptions;

import it.uniroma2.dicii.amod.onerjscheduling.entities.BnBProblem;

/**
 * This exception is raised when, at the end the computation, timeout is not reached and
 * one or more nodes are not closed.
 */
public class InvalidFinalStatusException extends RuntimeException {
    public InvalidFinalStatusException(BnBProblem p) {
        super("Node not closed at the end of the computation.\t" +
                "\tNode:\t" + p + "\n\tStatus:\t" + p.getStatus());
    }
}
