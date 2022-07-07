package it.uniroma2.dicii.amod.onerjscheduling.objectfunctions;

import it.uniroma2.dicii.amod.onerjscheduling.exceptions.InvalidObjectiveFunctionException;

public class ObjFunctionFactory {

    public ObjectFunction createObjFunction(ObjectFunctionEnum func) throws Exception {
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
