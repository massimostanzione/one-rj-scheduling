reset;

load	amplcsv.dll;

option	solver cplex;
option	cplex_options	'time=20000';
option	gurobi_options	'timelim=20000' 'bestbound=1';
model	1-rj-sumcj.mod;
data	1-rj-sumcj.dat;

#table 	jobs IN "amplcsv" "./../../data/10identical.csv": jobs <- [JOB_ID],RELEASE_DATE,PROCESSING_TIME;
table 	jobs IN "amplcsv" "./../../data/instances/generated/SIZE_SMALL_VARIANCE_SMALL_16.csv": jobs <- [JOB_ID],RELEASE_DATE,PROCESSING_TIME;

read 	table jobs;

# non troppo (rounding)
let M:=sum{j in jobs}(PROCESSING_TIME[j])+max{j in jobs}(RELEASE_DATE[j]);
#display sum{j in jobs}(PROCESSING_TIME[j]);
#display max{j in jobs}(RELEASE_DATE[j]);
solve;
printf "Big-M set to %d.\n", M;
printf "Final schedule:\n" ;
display RELEASE_DATE,PROCESSING_TIME,START_TIME,COMPLETION_TIME;
printf "Total completion time: %d\n", TOTAL_COMPLETION_TIME;




