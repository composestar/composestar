#include<stdio.h>
#include "Error.h"

extern int sp;
extern int bufp;

#define true 0;
#define false 1;

int stackFull(){
	if(sp==100){
		printf("Error: Stack full");
		return true;	
	}
	else return false;
}

int stackEmpty(){
	if(sp==0){
		printf("Error: Stack empty");
		return true;	
	}
	else return false;
}

int secondOpZero(){
	double op2;
	op2 = ST_pop();
        if(op2==0.0){
        	printf("Error: second Operand zero");        
        	return false;
        }
}

int bufferFull(){
	if(bufp==100){
		printf("Error: Buffer full");
		return true;	
	}	
	else return false;
}