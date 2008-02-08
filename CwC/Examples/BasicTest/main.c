/**
 * A simply test case for Compose* / CwC
 *  
 * $Id$  
 */
  
#include <stdio.h>
#include "calc.h"
#include "ComposeStar.h"

void beforeSomeThing(JoinPointContext* jpc)
{
	printf("Executing something before: %s\n", jpc->startSelector);
}

void afterSomeThing(JoinPointContext* jpc)
{
	printf("Executing something after: %s\n", jpc->startSelector);
}

void printPlainHeader()
{
	printf("Compose*/CwC Basic Test Example\n\n");
}

/**
 * Print the header with a version string. This is not safe for system tests.
 */
void printHeaderWithVersion()
{
	printf("Compose*/CwC Basic Test Example\n$Id$\n\n");
}


void printNoName(char* name)
{}

void printProgramName(char* name)
{
	printf("argv[0] = %s\n", name);
}

static int __argc;

int doPrintProgramName()
{
	printf("doPrintProgramName = %d\n", __argc > 1);
	return __argc > 1;
}

int main(int argc, char *argv[])
{
	__argc = argc;
	printHeaderWithVersion();
	
  printProgramName(argv[0]);
	
	printf("%d+%d=%d\n", 5, 5, add(5,5));
	printf("%d*%d=%d\n", 5, 5, div(5,5));
	// this is ofcourse a divide by zero
	printf("%d*%d=%d\n", 5, 0, div(5,0));
	
	return 0;
}
