package it.uniroma2.dicii.amod.onerjscheduling.objectfunctions;

import it.uniroma2.dicii.amod.onerjscheduling.scheduling.SRPTSchedulingRule;
import it.uniroma2.dicii.amod.onerjscheduling.scheduling.Schedule;
import it.uniroma2.dicii.amod.onerjscheduling.scheduling.ScheduleItem;

public class TotalCompletionTime extends ObjectFunction {

    @Override
    protected void initMathNotation() {
        this.mathNotation = "\\sum C_j";
    }

    @Override
    protected void initRelaxedProblemRule() {
        this.relaxedProblemRule = new SRPTSchedulingRule();
    }

    @Override
    public void initName() {
        this.name = ObjectFunctionEnum.SUM_COMPLETION_TIMES;
    }
    @Override
    public void initAmplString() {
        this.amplString = "TOTAL_COMPLETION_TIME";
    }

    public int compute(Schedule s) {
        int ret = 0;
        for (ScheduleItem i : s.getItems()) {
            // se il job nella schedula è non-pmnt, vuol dire che ha completato la sua intera esecuzione
            if (i.isPreempted() == false)
                ret += i.getFinishTime();
        }
        return ret;
    }
}
