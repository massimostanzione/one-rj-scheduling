# NOTICE: this script is NOT intended to be run with any AMPL instance:
#         This is a basis for a script that is dynamically built inside
#	      the execution of the Java project.
#         If run into an AMPL instance, it will return an error.

#reset;

load	amplcsv.dll;

#option	solver cplex;
model	./src/ampl/1-rj-sumcj.mod;
data	./src/ampl/1-rj-sumcj.dat;

table 	jobs IN "amplcsv" $PATH: jobs <- [JOB_ID],RELEASE_DATE,PROCESSING_TIME;

read 	table jobs;

# non troppo (rounding)
let M:=sum{j in jobs}(PROCESSING_TIME[j])+max{j in jobs}(RELEASE_DATE[j]);
#display sum{j in jobs}(PROCESSING_TIME[j]);
#display max{j in jobs}(RELEASE_DATE[j]);
solve;
#display bestbound;
printf "Big-M set to %d.\n", M;
#printf "Final schedule:\n" ;
#display RELEASE_DATE,PROCESSING_TIME,START_TIME,COMPLETION_TIME;
#printf "Total completion time: %d\n", TOTAL_COMPLETION_TIME;




