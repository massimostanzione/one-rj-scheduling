package it.uniroma2.dicii.amod.onerjscheduling.objectfunctions;

import it.uniroma2.dicii.amod.onerjscheduling.exceptions.InvalidObjectiveFunctionException;

/**
 * A <i>factory</i> for <code>ObjectiveFunciont</code> object generation.
 */
public class ObjFunctionFactory {
    public ObjectiveFunction createObjFunction(ObjectFunctionEnum func) {
        switch (func) {
            case SUM_COMPLETION_TIMES:
                return new TotalCompletionTime();
            case DUMMY:
                return new DummyObjFunction();
            default:
                throw new InvalidObjectiveFunctionException(func);
        }
    }
}
