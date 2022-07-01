package it.uniroma2.dicii.amod.onerjscheduling.objectfunctions;

public class ObjFunctionFactory {

    public ObjectFunction createObjFunction(ObjectFunctionEnum func) throws Exception {
        switch (func) {
            case SUM_COMPLETION_TIMES:
                return new TotalCompletionTime();
            case DUMMY:
                return new DummyObjFunction();
            default:
                throw new Exception("Invalid object function : " + func);
        }
    }
}
