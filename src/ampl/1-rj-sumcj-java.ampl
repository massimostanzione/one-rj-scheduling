# NOTICE: this script is NOT intended to be run with any AMPL instance:
#         This is a basis for a script that is dynamically built inside
#	      the execution of the Java project.
#         If run into an AMPL instance, it will return an error.

load	amplcsv.dll;

model	./src/ampl/1-rj-sumcj.mod;
data	./src/ampl/1-rj-sumcj.dat;

table 	jobs IN "amplcsv" $PATH: jobs <- [JOB_ID],RELEASE_DATE,PROCESSING_TIME;

read 	table jobs;

let M	:=	sum{j in jobs}(PROCESSING_TIME[j])+max{j in jobs}(RELEASE_DATE[j]);

printf "Big-M set to %d.\n", M;

solve;

