package it.uniroma2.dicii.amod.onerjscheduling.objectfunctions;

import it.uniroma2.dicii.amod.onerjscheduling.scheduling.SRPTSchedulingRule;
import it.uniroma2.dicii.amod.onerjscheduling.scheduling.Schedule;
import it.uniroma2.dicii.amod.onerjscheduling.scheduling.ScheduleItem;

/**
 * Sum of all the completion times. It has SRPT as relaxed problem rule.
 */
public class TotalCompletionTime extends ObjectiveFunction {

    @Override
    protected void initMathNotation() {
        this.mathNotation = "\\sum C_j";
    }

    @Override
    protected void initRelaxedProblemRule() {
        this.relaxedProblemRule = new SRPTSchedulingRule();
    }

    @Override
    public void initAmplString() {
        this.amplString = "TOTAL_COMPLETION_TIME";
    }

    @Override
    public void initName() {
        this.name = ObjectFunctionEnum.SUM_COMPLETION_TIMES;
    }

    /**
     * Compute finishing time for a schedule.
     *
     * @param s schedule
     * @return finishing time for the schedule <code>s</code>.
     */
    public int compute(Schedule s) {
        int ret = 0;
        for (ScheduleItem i : s.getItems()) {
            // if the job in the schedule is non-pmnt, it has completed its execution.
            if (!i.isPreempted())
                ret += i.getFinishTime();
        }
        return ret;
    }
}
