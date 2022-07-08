package it.uniroma2.dicii.amod.onerjscheduling.solvers;

import com.ampl.AMPL;
import com.ampl.Environment;
import com.ampl.Objective;
import it.uniroma2.dicii.amod.onerjscheduling.entities.DataInstance;
import it.uniroma2.dicii.amod.onerjscheduling.entities.ExecutionReportItem;
import it.uniroma2.dicii.amod.onerjscheduling.objectfunctions.ObjectFunction;
import it.uniroma2.dicii.amod.onerjscheduling.utils.ExternalConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;

public abstract class AMPLSolver extends Solver {
    private AMPL amplInstance;
    private String specificSolverOptionsPrefix;
    private String specificSolverOptions;

    public AMPL getAmplInstance() {
        return this.amplInstance;
    }
    public AMPLSolver() {
        this.specificSolverOptions=this.initSpecificSolverOptions();
        this.specificSolverOptionsPrefix=this.initSpecificSolverOptionsPrefix();
        this.initializeSolverParams(null);
    }
protected abstract String initSpecificSolverOptionsPrefix();
protected abstract String initSpecificSolverOptions();

    public void initializeSolverParams(DataInstance instance) {
        // serve anche da reset nel caso una precedente esecuzione dovesse andare in timeout
        Environment env = new Environment(ExternalConfig.getSingletonInstance().getAmplPath());
        this.amplInstance = new AMPL(env);
    }

    @Override
    public ExecutionReportItem solveExecutive(Instant start, ObjectFunction objFn, DataInstance instance) {
        int solution = -1;
        try {
            this.amplInstance.setOption("solver", this.initAMPLImplSolverName());
            this.amplInstance.setOption(this.specificSolverOptionsPrefix, this.specificSolverOptions);
            this.amplInstance.eval(this.loadAMPLfile("./src/ampl/1-rj-sumcj-java.ampl", instance.getPath()));
            //this.amplInstance.eval("table jobs IN \"amplcsv\" \"" + instance.getPath() + "\": jobs <- [JOB_ID],RELEASE_DATE,PROCESSING_TIME;");
           // this.amplInstance.eval(this.loadAMPLfile("./src/ampl/java/1-rj-sumcj-java.ampl"));
           // this.amplInstance.eval("read table jobs;");
          //  this.amplInstance.eval("let M:=sum{j in jobs}(PROCESSING_TIME[j])+max{j in jobs}(RELEASE_DATE[j]);");
           // this.amplInstance.eval("printf \"Big-M set to %d\\n\", M ;");
           // this.amplInstance.solve();
          //  this.amplInstance.eval("display RELEASE_DATE,PROCESSING_TIME,START_TIME,COMPLETION_TIME;");
            Objective objFnVal = this.amplInstance.getObjective(objFn.getAmplString());
            solution = (int) objFnVal.get().value();
        } catch (RuntimeException e) {
            e.printStackTrace();
           /* System.out.println("Timeout.");
            this.amplInstance.close();
            return -1;*/
        } finally {
            // close all AMPL related resources here
            this.amplInstance.close();
        }
        return new ExecutionReportItem(solution, -1);
    }

    protected abstract String initAMPLImplSolverName();
private String loadAMPLfile(String filePath, String instPath){
String script="";
    Path file = Path.of(filePath);
    try {
        script = Files.readString(file);
    } catch (IOException e) {
        e.printStackTrace();
    }
    return script.replace("$PATH", "\""+instPath+"\"");
}
    @Override
    public void printStats() {
    }
}
