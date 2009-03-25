/**
 * A simply test case for Compose* / CwC
 *  
 * $Id$  
 */
  
#include <stdio.h>
#include "calc.h"
#include "ComposeStar.h" // for JPC

int __argc;

void beforeSomeThing(JoinPointContext* jpc)
{
	printf("Executing something before: %s\n", jpc->startSelector);
}

void afterSomeThing(JoinPointContext* jpc)
{
	printf("Executing something after: %s\n", jpc->startSelector);
}

/**
 * Print the header with a version string. This is not safe for system tests.
 */
void printHeaderWithVersion()
{
	printf("Compose*/CwC Basic Test Example\n$Id$\n\n");
}

void printPlainHeader()
{
	printf("Compose*/CwC Basic Test Example\n\n");
}

void printProgramName(char* name)
{
	printf("argv[0] = %s\n", name);
}

void printNoName(char* name)
{}

int doPrintProgramName()
{
	printf("doPrintProgramName = %d\n", __argc > 1);
	return __argc > 1;
}

int main(int argc, char *argv[])
{
	int (*funcPtr)(char* name) = &printProgramName;  
	
	__argc = argc;
	
	printHeaderWithVersion();
	
  printProgramName(argv[0]);
  
  // function pointer test (ignored by Compose* because the message is unknown)
  funcPtr(argv[0]);
	
	printf("%d+%d=%d\n", 5, 5, add(5,5));
	printf("%d*%d=%d\n", 5, 5, divide(5,5));
	// this is ofcourse a divide by zero
	printf("%d*%d=%d\n", 5, 0, divide(5,0));
	
	// multiply directs to mult using dispatch filter
	// FIXME: the following doesn't work anymore because the target is unknown
	// this is an issue with Compose* and C
	//printf("multiply(%d,%d)=%d\n", 5, 5, multiply(5,5));
	
	// this directys to printNoName, used for testing of different function signs
	newPrint(argv[0]);
	
	printf("Primes up to %u : %u\n", 10000000, nsieve(10000000));
	printf("Primes up to %u : %u\n", 20000000, nsieve(20000000));
	
	return 0;
}
