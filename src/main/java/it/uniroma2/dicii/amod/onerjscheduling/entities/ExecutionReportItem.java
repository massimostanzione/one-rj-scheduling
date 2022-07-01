package it.uniroma2.dicii.amod.onerjscheduling.entities;

import it.uniroma2.dicii.amod.onerjscheduling.objectfunctions.ObjectFunctionEnum;
import it.uniroma2.dicii.amod.onerjscheduling.solvers.SolverEnum;
import it.uniroma2.dicii.amod.onerjscheduling.utils.ExportableAsDatasetRecord;

import java.util.List;

public class ExecutionReportItem extends ExportableAsDatasetRecord {
    private String dataPath;
    private ObjectFunctionEnum objFunction;
    private SolverEnum solverName;
    private int solution;
    private long time;  //millis

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public ObjectFunctionEnum getObjFunction() {
        return objFunction;
    }

    public void setObjFunction(ObjectFunctionEnum objFunction) {
        this.objFunction = objFunction;
    }

    public SolverEnum getSolverName() {
        return solverName;
    }

    public void setSolverName(SolverEnum solverName) {
        this.solverName = solverName;
    }

    public int getSolution() {
        return solution;
    }

    public void setSolution(int solution) {
        this.solution = solution;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "ExecutionReportItem{" +
                "dataPath='" + dataPath + '\'' +
                ", objFunction=" + objFunction +
                ", solverName=" + solverName +
                ", solution=" + solution +
                ", time=" + time +
                '}';
    }

    @Override
    public List<List<String>> getDatasetAttributes() {
        this.setDatasetAttributes("OBJECT_FUNCTION", "INSTANCE_PATH", "SOLVER", "SOLUTION", "TIME_MS");
        return this.datasetAttributes;
    }

    @Override
    public List<List<String>> getDatasetRecord() {
        this.setDatasetRecord(this.objFunction, this.dataPath, this.solverName, this.solution == -1 ? "TIMEOUT" : this.solution, this.time == -1 ? "TIMEOUT" : this.time);
        return this.datasetRecord;
    }
}
