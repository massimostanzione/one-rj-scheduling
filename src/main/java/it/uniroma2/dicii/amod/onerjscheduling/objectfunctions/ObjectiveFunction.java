package it.uniroma2.dicii.amod.onerjscheduling.objectfunctions;

import it.uniroma2.dicii.amod.onerjscheduling.entities.Job;
import it.uniroma2.dicii.amod.onerjscheduling.scheduling.Schedule;
import it.uniroma2.dicii.amod.onerjscheduling.scheduling.SchedulingRule;

import java.util.List;

public abstract class ObjectiveFunction {
    protected ObjectFunctionEnum name;
    protected String mathNotation;
    protected SchedulingRule relaxedProblemRule;
    protected String amplString;

    public ObjectiveFunction() {
        this.initName();
        this.initMathNotation();
        this.initRelaxedProblemRule();
        this.initAmplString();
    }

    public abstract void initName();

    protected abstract void initMathNotation();

    protected abstract void initRelaxedProblemRule();

    protected abstract void initAmplString();

    public String getAmplString() {
        return this.amplString;
    }

    public String getMathNotation() {
        return mathNotation;
    }

    public ObjectFunctionEnum getName() {
        return name;
    }

    public Schedule computeRelaxedSchedule(Schedule fullInitialSchedule, List<Job> jobList) {
        return relaxedProblemRule.execute(fullInitialSchedule, jobList);
    }

    public abstract int compute(Schedule s);
}
