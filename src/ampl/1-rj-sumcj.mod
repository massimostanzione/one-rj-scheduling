set		jobs;

param 	RELEASE_DATE 	{jobs} 			>=	0 	integer;
param 	PROCESSING_TIME {jobs} 			>=	0 	integer; 
param 	M 								>=	0 	integer;

var 	x 				{jobs, jobs} 			binary;	
var		START_TIME 		{jobs} 			>=	0	integer;     
var 	COMPLETION_TIME {jobs} 			>=	0 	integer;
#var M							>=0 	integer;
#var count >=0 integer;

minimize TOTAL_COMPLETION_TIME : 
	sum {j in jobs} COMPLETION_TIME[j];

subject to CONSTR_RELEASE_TIME {j in jobs}:
    START_TIME[j]>=
    RELEASE_DATE[j];
    
subject to CONSTR_COMPLETION_CONSISTENCY {j in jobs}:
	COMPLETION_TIME[j]	>=
	START_TIME[j]+PROCESSING_TIME[j];
	
subject to CONSTR_DISJUNCTIVE_1{j in jobs, k in jobs: j != k}:
	COMPLETION_TIME[j]+PROCESSING_TIME[k] <=
	COMPLETION_TIME[k]+M*(1-x[j,k]);
	
subject to CONSTR_DISJUNCTIVE_2{j in jobs, k in jobs: j != k}:
	COMPLETION_TIME[k]+PROCESSING_TIME[j]<=
	COMPLETION_TIME[j]+M*(x[j,k]);
