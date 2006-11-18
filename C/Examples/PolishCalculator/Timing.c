#include "Timing.h"


void TIMING_in(char* functionName, time_t time1)
{
	printf("Time 1 function: %s = %i\n", functionName, time1);	
}

void TIMING_out(char* functionName, time_t time2){
	printf("Time 2 function: %s = %i\n", functionName, time2);	
}