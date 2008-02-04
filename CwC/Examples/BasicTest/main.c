/**
 * A simply test case for Compose* / CwC
 *  
 * $Id$  
 */
  
#include <stdio.h>
#include "calc.h"

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

void printProgramName(char* name)
{
	printf("argv[0] = %s\n", name);
}

void printVoid(char* name)
{}

int doPrintProgramName()
{
	return 0;
}

int main(int argc, char *argv[])
{
	printHeaderWithVersion();
	
  printProgramName(argv[0]);
	
	printf("%d+%d=%d\n", 5, 5, add(5,5));
	printf("%d*%d=%d\n", 5, 5, div(5,5));
	// this is ofcourse a divide by zero
	printf("%d*%d=%d\n", 5, 0, div(5,0));
    
	return 0;
}
