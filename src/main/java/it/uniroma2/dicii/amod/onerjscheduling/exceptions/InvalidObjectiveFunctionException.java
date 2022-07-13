package it.uniroma2.dicii.amod.onerjscheduling.exceptions;

import it.uniroma2.dicii.amod.onerjscheduling.objectfunctions.ObjectFunctionEnum;

/**
 * This exception is raised when a non valid object function is asked to be created from <code>ObjFunctionFactory</code>.
 */
public class InvalidObjectiveFunctionException extends RuntimeException {
    public InvalidObjectiveFunctionException(ObjectFunctionEnum func) {
        super("Invalid object function for the value: " + func);
    }
}
