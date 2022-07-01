reset;

load	amplcsv.dll;

option	solver cplex;

model	1-rj-sumcj.mod;
data	1-rj-sumcj.dat;

table 	jobs IN "amplcsv" "./../../data/mortonPentico-ljb12-reduced.csv": jobs <- [JOB_ID],RELEASE_DATE,PROCESSING_TIME;

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




