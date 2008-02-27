/**
 * $Id$  
 */

/*$annotation {} ApplyCaching[module]; $*/
/*$annotation {} CacheResult[function]; $*/
/*$annotation {} InvalidateCache[function]; $*/

/*$ApplyCaching$*/

#include <stdio.h>

int fibonacci(int x)
/*$CacheResult$*/
{
	if (x <= 0) return -1;
	if (x <= 2) return 1;
	return (fibonacci(x-1) + fibonacci(x-2));
}

int main(int argc, char *argv[])
{
	printf("Calculating the 5th fibonacci number: \n");
	printf("%d\n", fibonacci(5));

	printf("Calculating the 6th fibonacci number: \n"); 
	printf("%d\n", fibonacci(6));

	printf("Calculating the 4th fibonacci number: \n"); 
	printf("%d\n", fibonacci(4));

	printf("Calculating the 44th fibonacci number (this may take a while)\n");
	printf("%d\n", fibonacci(44));
	
	return 0;
}
