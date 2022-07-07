package it.uniroma2.dicii.amod.onerjscheduling.exceptions;

import it.uniroma2.dicii.amod.onerjscheduling.objectfunctions.ObjectFunctionEnum;

public class InvalidObjectiveFunctionException extends RuntimeException {
    public InvalidObjectiveFunctionException(ObjectFunctionEnum func) {
        super("Invalid object function for the value: " + func);
    }
}
