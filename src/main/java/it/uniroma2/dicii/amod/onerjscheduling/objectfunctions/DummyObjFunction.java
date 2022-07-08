package it.uniroma2.dicii.amod.onerjscheduling.objectfunctions;

import it.uniroma2.dicii.amod.onerjscheduling.scheduling.DummySchedulingRule;
import it.uniroma2.dicii.amod.onerjscheduling.scheduling.Schedule;

public class DummyObjFunction extends ObjectFunction {

    @Override
    protected void initMathNotation() {
        this.mathNotation = "dummy";
    }

    @Override
    protected void initRelaxedProblemRule() {
        this.relaxedProblemRule = new DummySchedulingRule();
    }

    @Override
    protected void initAmplString() {
        this.amplString="DUMMY";
    }

    @Override
    public void initName() {
        this.name = ObjectFunctionEnum.DUMMY;
    }

    @Override
    public int compute(Schedule s) {
        return 42;
    }
}
