#ifndef TIMING_H
#define TIMING_H
#include <time.h>
#include <stdio.h>

void TIMING_in(char* functionName, time_t time1);

void TIMING_out(char* functionName, time_t time2);

#endif