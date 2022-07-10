package it.uniroma2.dicii.amod.onerjscheduling.solvers;

import it.uniroma2.dicii.amod.onerjscheduling.control.Scheduler;
import it.uniroma2.dicii.amod.onerjscheduling.entities.BnBProblem;
import it.uniroma2.dicii.amod.onerjscheduling.entities.Instance;
import it.uniroma2.dicii.amod.onerjscheduling.entities.output.InstanceExecResult;
import it.uniroma2.dicii.amod.onerjscheduling.objectfunctions.ObjectFunction;

import java.time.Instant;
import java.util.List;

public class BnBLLBSolver extends BnBDFSSolver {

    @Override
    public SolverEnum initName() {
        return SolverEnum.BRANCH_AND_BOUND_LLB;
    }

    @Override
    public InstanceExecResult solveExecutive(Instant start, ObjectFunction objFn, Instance instance) {

        //this.allNodes.add(rootBnBProblem);


        Scheduler sch = new Scheduler();
        // QUI GUARDO LA LISTA COME LIVELLO
        int min;
        while (this.openBnBProblems.size() > 0) {
            if (checkTimeout(start))
                return new InstanceExecResult(this.incumbent, this.globLB);
            min = Integer.MAX_VALUE;
            int res = -1;
            for (BnBProblem p : this.openBnBProblems) {
                if (checkTimeout(start))
                    return new InstanceExecResult(this.incumbent, this.globLB);
           /*     CONDIZIONE MAI VERIFICATA
           if (p.getSolution() != null) {

                    System.out.println("STATO POST-PREPROCESSAMENTO "+p.getStatus());
                     p.setExpanded();
                    // continue;
                }
                //if(p.getSolution()==null)
                else*/
                if(!p.isVisited())
                    p = this.examineProblem(p, objFn);
                if (checkTimeout(start))
                    return new InstanceExecResult(this.incumbent, this.globLB);
                if(p.isOptimalByLB())
                    return new InstanceExecResult(this.incumbent, this.globLB);
              //  this.recordForStats(p);
                res = p.getSolution();
               // System.out.println("ESAMINATO CON SOLUZIONE: "+p);
                //ora posso decidere la logica di espansione
                if (p.isWaitingForFurtherProcessing()) {
                    // se la schedula corrente non è già in PEICL, aggiungila
                    if (!sch.scheduleInProblemListByFullInitSchedule(p.getFullInitialSchedule(), this.potentiallyExpandableInCurrentLevel))
                        this.potentiallyExpandableInCurrentLevel.add(p);
                    if (res < min && res != -1) min = res;
                    if (!this.potentiallyExpandableInCurrentLevel.contains(p)) {
                        //   System.out.println("potenzialmente espandibile "+p.getSolution()+" "+p.getFullInitialSchedule());
                        this.potentiallyExpandableInCurrentLevel.add(p);
                    }
                }
                //else System.out.println(p.getStatus());
            }

            // aggiorno il livello
            this.openBnBProblems.removeAll(this.openBnBProblems);
            // System.out.println("Min = "+min);
            for (BnBProblem p : this.potentiallyExpandableInCurrentLevel) {
                if (checkTimeout(start))
                    return new InstanceExecResult(this.incumbent, this.globLB);
                //this.openBnBProblems.add(p);// NO!
                // System.out.println("\nguardo "+p.getSolution()+" avente schedula "+p.getFullInitialSchedule());
                if (p.getSolution().equals(min) || p.isRoot()) {
                    //  System.out.println("\tlo espando");
                    //  this.openBnBProblems.remove(p);
                    //p.setExpanded();
                    //this.openBnBProblems.add(p);
                    //this.openBnBProblems.addAll(this.generateSubProblems(p));

                    List<BnBProblem> sub = this.generateSubProblems(p, start);
                    if (checkTimeout(start))
                        return new InstanceExecResult(this.incumbent, this.globLB);
                    if (sub != null){
                        this.openBnBProblems.addAll(sub);
                    //this.allNodes.addAll(sub);
                    }
                  //  this.updateStatuses(p.getStatus());




                     p.setExpanded();
                } else {
                    if(p.isWaitingForFurtherProcessing()){
                        this.openBnBProblems.add(p);
                       // this.allNodes.add(p);
                    }
                    //p.setStatus(FATHOMED_BOUNDING);
                    // System.out.println("\tlo scarto");
                }
                //this.updateStatuses(p.getStatus());
            }
            // this.potentiallyExpandable.removeAll(this.potentiallyExpandable);
        }
        return new InstanceExecResult(this.incumbent, this.globLB);
    }

}
