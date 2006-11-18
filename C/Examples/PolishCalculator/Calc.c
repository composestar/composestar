#include <time.h>
#include "Calc.h"

struct annotatie{
	char* x;
};

typedef struct annotatie in;

typedef struct annotatie out;

typedef struct annotatie inout;

int calcDivide($(inout("Semantic"))double* answer, $(in("Semantic"))double op1, $(in("Semantic"))double op2)
{	
	int result=OK;
	
	if(op2 != 0)
	 	*answer = op1 / op2;	
	else 
		result =ERROR;
		
	return result;	 
}

int calcMinus($(inout("Semantic"))double* answer, $(in("Semantic"))double op1, $(in("Semantic"))double op2)
{
	int result=OK;
	*answer = op1 - op2;	
	return result;	 
}

int calcTimes($(inout("Semantic"))double* answer, $(in("Semantic"))double op1, $(in("Semantic"))double op2)
{
	int result=OK;
	*answer = op1*op2;	
	return result;	 
}

int calcAdd($(inout("Semantic"))double* answer, $(in("Semantic"))double op1, $(in("Semantic"))double op2)
{
	int result=OK;
	*answer = op1+op2;	
	return result;	 
}

int calcModulo($(inout("Semantic"))double* answer, $(in("Semantic"))double op1, $(in("Semantic"))double op2)
{
	int result=OK;
	
	if(op2 != 0)
		*answer = (int)op1%(int)op2;	
	else 
		result =ERROR;
		
	return result;	 
}

