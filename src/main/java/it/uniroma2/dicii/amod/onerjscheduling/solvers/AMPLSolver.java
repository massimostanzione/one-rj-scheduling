package it.uniroma2.dicii.amod.onerjscheduling.solvers;

import com.ampl.AMPL;
import com.ampl.Environment;
import com.ampl.Objective;
import it.uniroma2.dicii.amod.onerjscheduling.utils.ExternalConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

public abstract class AMPLSolver extends Solver {
    public AMPL amplInstance;

    public AMPL getAmplInstance() {
        return this.amplInstance;
    }
    public AMPLSolver() {
        this.initializeSolverParams();
    }

    public void initializeSolverParams() {
        // serve anche da reset nel caso una precedente esecuzione dovesse andare in timeout
        Environment env = new Environment(ExternalConfig.getSingletonInstance().getAmplPath());
        this.amplInstance = new AMPL(env);
    }

    @Override
    public int solveExecutive(Instant start) {
        int solution = -1;
        try {
            this.amplInstance.setOption("solver", this.getAMPLImplSolver());
            Path filePath = Path.of("./src/ampl/1-rj-sumcj-java.ampl");
            String script = Files.readString(filePath);
            this.amplInstance.eval(script);
            this.amplInstance.eval("table jobs IN \"amplcsv\" \"" + this.path + "\": jobs <- [JOB_ID],RELEASE_DATE,PROCESSING_TIME;");
            this.amplInstance.eval("read table jobs;");
            this.amplInstance.eval("let M:=sum{j in jobs}(PROCESSING_TIME[j])+max{j in jobs}(RELEASE_DATE[j]);");
            this.amplInstance.eval("printf \"Big-M set to %d\\n\", M ;");
            this.amplInstance.solve();
            Objective objFnVal = this.amplInstance.getObjective("TOTAL_COMPLETION_TIME");
            solution = (int) objFnVal.get().value();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            System.out.println("Timeout.");
            this.amplInstance.close();
            return -1;
        } finally {
            // close all AMPL related resources here
            this.amplInstance.close();
        }
        return solution;
    }

    protected abstract String getAMPLImplSolver();

    @Override
    public void printStats() {
    }
}
