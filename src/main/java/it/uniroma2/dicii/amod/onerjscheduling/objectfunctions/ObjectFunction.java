package it.uniroma2.dicii.amod.onerjscheduling.objectfunctions;

import it.uniroma2.dicii.amod.onerjscheduling.entities.Job;
import it.uniroma2.dicii.amod.onerjscheduling.scheduling.Schedule;
import it.uniroma2.dicii.amod.onerjscheduling.scheduling.SchedulingRule;

import java.util.List;

public abstract class ObjectFunction {
    protected ObjectFunctionEnum name;
    protected String mathNotation;
    protected SchedulingRule relaxedProblemRule;
    protected String amplString;

    public String getAmplString() {
        return this.amplString;
    }

    public ObjectFunction() {
        this.initName();
        this.initMathNotation();
        this.initRelaxedProblemRule();
        this.initAmplString();
    }

    protected abstract void initMathNotation();

    protected abstract void initRelaxedProblemRule();
    protected abstract void initAmplString();

    public String getMathNotation() {
        return mathNotation;
    }

    public ObjectFunctionEnum getName() {
        return name;
    }

    public abstract void initName();

    public Schedule computeRelaxedSchedule(Schedule fullInitialSchedule, List<Job> jobList) {
        return relaxedProblemRule.execute(fullInitialSchedule, jobList);
    }

    public abstract int compute(Schedule s);
}
