package it.uniroma2.dicii.amod.onerjscheduling.solvers;

import com.ampl.AMPL;
import com.ampl.Environment;
import com.ampl.Objective;
import it.uniroma2.dicii.amod.onerjscheduling.entities.Instance;
import it.uniroma2.dicii.amod.onerjscheduling.entities.output.InstanceExecResult;
import it.uniroma2.dicii.amod.onerjscheduling.objectfunctions.ObjectiveFunction;
import it.uniroma2.dicii.amod.onerjscheduling.utils.ExternalConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

/**
 * AMPL commercial solver, to be specialized with specific AMPL solver.
 */
public abstract class AMPLSolver extends Solver {
    private final String specificSolverOptionsPrefix;
    private final String specificSolverOptions;
    private AMPL amplInstance;

    public AMPLSolver() {
        this.specificSolverOptions = this.initSpecificSolverOptions();
        this.specificSolverOptionsPrefix = this.initSpecificSolverOptionsPrefix();
        this.initializeSolverParams(null);
    }

    protected abstract String initSpecificSolverOptionsPrefix();

    protected abstract String initSpecificSolverOptions();

    @Override
    public InstanceExecResult solveExecutive(Instant start, ObjectiveFunction objFn, Instance instance) {
        int solution = -1;
        try {
            // load AMPL file containing all the AMPL instructions
            this.amplInstance.setOption("solver", this.initAMPLImplSolverName());
            this.amplInstance.setOption(this.specificSolverOptionsPrefix, this.specificSolverOptions);
            this.amplInstance.eval(this.loadAMPLfile("./src/ampl/1-rj-sumcj-java.ampl", instance.getPath()));
            Objective objFnVal = this.amplInstance.getObjective(objFn.getAmplString());
            solution = (int) objFnVal.get().value();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            this.amplInstance.close();
        }
        return new InstanceExecResult(solution, 0);
    }

    protected abstract String initAMPLImplSolverName();

    public void initializeSolverParams(Instance instance) {
        // also works as "reset" if a previous execution reaches timeout
        Environment env = new Environment(ExternalConfig.getSingletonInstance().getAmplPath());
        this.amplInstance = new AMPL(env);
    }

    @Override
    public void printStats(boolean timeout) {
        // nothing needed, specialized solvers already print execution details by default.
    }

    private String loadAMPLfile(String filePath, String instPath) {
        String script = "";
        Path file = Path.of(filePath);
        try {
            script = Files.readString(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return script.replace("$PATH", "\"" + instPath + "\"");
    }
}
