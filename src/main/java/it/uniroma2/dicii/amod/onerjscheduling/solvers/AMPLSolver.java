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
    //private AMPL ampl;

    public AMPLSolver() {
        //TODO parametrizzare
        this.initializeSolverParams();
    }

    public void initializeSolverParams() {
        // serve anche da reset nel caso una precedente esecuzione dovesse andare in timeout
        Environment env = new Environment(ExternalConfig.getSingletonInstance().getAmplPath());
        this.ampl = new AMPL(env);
    }

    @Override
    public int solveExecutive(Instant start) {
        int solution = -1;
        try {
            this.ampl.setOption("solver", this.getAMPLImplSolver());
            Path filePath = Path.of("./src/ampl/1-rj-sumcj-java.ampl");
            String script = Files.readString(filePath);
            this.ampl.eval(script);
            this.ampl.eval("table jobs IN \"amplcsv\" \"" + this.path + "\": jobs <- [JOB_ID],RELEASE_DATE,PROCESSING_TIME;");
            this.ampl.eval("read table jobs;");
            this.ampl.eval("let M:=sum{j in jobs}(PROCESSING_TIME[j])+max{j in jobs}(RELEASE_DATE[j]);");
            this.ampl.eval("printf \"Big-M set to %d\\n\", M ;");
            this.ampl.solve();
            Objective objFnVal = this.ampl.getObjective("TOTAL_COMPLETION_TIME");
            solution = (int) objFnVal.get().value();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            System.out.println("Timeout.");
            this.ampl.close();
            return -1;
        } finally {
            // close all AMPL related resources here
            this.ampl.close();
        }
        return solution;
    }

    protected abstract String getAMPLImplSolver();

    @Override
    public void printStats() {
    }
}
