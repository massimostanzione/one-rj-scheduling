# one-rj-scheduling
Project for the exam "Algoritmi e Modelli per l'Ottimizzazione Discreta"

This software is produced for the final project of the exam _Algoritmi e Modelli per l'Ottimizzazione Discreta_ (Discrete Optimization).

It is meant to make (possibly) easier the comparison of the performance of different solvers (AMPL and *branch-and-bound*) for $1|r_j|f$ scheduling problems, where $f$ is an objective function.

More specifically, the main idea of this program is that, given in input:
- an objective function $f$ (with associated optimal rule for solving the $1|r_j, pmnt|f$ relaxation);
- one or more job instances;
- a set of solvers (some AMPL/commercial ones, and some *branch-and-bound* ones)

the program algorithm will work as follows:
```
for each instance
	for each solver
		solve the 1|r_j|f problem
```
Also, for each execution it produces some statistics about the execution themselves and about the average performance of each solver.
## Prerequisites
In order to make possible the execution of the AMPL solvers, please make sure that you have a valid AMPL installation, and that you have successifully linked the [AMPL Java API](https://ampl.com/resources/api/#Linux "AMPL Java API") to the project and properly set the `AMPL_PATH` variable into the `config.ini` configuration file.
## Solvers
Implemented solvers:
- AMPL Solvers (via AMPL Java API)
	- CPLEX;
	- Gurobi.
- *Branch-and-bound* solvers:
	- *Branch-and-bound* "Full";
	- *Branch-and-bound* "FIFO";
	- *Branch-and-bound* "Forward";
	- *Branch-and-bound* "LLB".


## Input
### Input formatting
Input must be provided via `.csv` files structured as follows:
```
ID,PROCESSING_TIME,RELEASE_DATE
```
where each field must be integer.
Files will be referred into the `OneRjScheduling` main class.
### Instance generator
Input instances can also be generated via the `InstanceGenerator` class. It can be run *stand-alone* to generate pseudocasual job instances based on three parameters:
- Instance size (number of jobs);
- Variance of the mean processing time $p_j$;
- Method of generating release dates $r_j$.

Output will be saved into the `/data/instances/generated` folder.

## Output format
The program produces two `.csv` files in the default `/output/report` folder:
- `report-<name>-<datetime>-executions.csv` contains some information about all the single solver executions;
- `report-<name>-<datetime>.csv` contains some average statistics about the performance of each solver.

## Execution environment
This software was developed with IntelliJ IDEA 2021.2.3 (Community Edition) on Linux Mint 20.2
