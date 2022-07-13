package it.uniroma2.dicii.amod.onerjscheduling.exceptions;

/**
 * This exception is raised when there is a status that cannot be set for a node in a specific context.
 */
public class InconsistentStatusException extends RuntimeException {
    public InconsistentStatusException(String s) {
        super(s);
    }
}
