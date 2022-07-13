package it.uniroma2.dicii.amod.onerjscheduling.exceptions;

/**
 * This exception is raised whenever there is some kind of problem with time computation.
 */
public class InvalidTimeException extends RuntimeException {
    public InvalidTimeException(String s) {
        super(s);
    }
}
