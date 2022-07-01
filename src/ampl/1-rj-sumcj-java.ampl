# NOTICE: this script is NOT intended to be run with any AMPL instance:
#         it is only a "constant" part of a script that is dynamically
#         built inside the execution of the Java project.
#         If run into an AMPL instance, it will return an error.

reset;

load	amplcsv.dll;

model	./src/ampl/1-rj-sumcj.mod;
data	./src/ampl/1-rj-sumcj.dat;
