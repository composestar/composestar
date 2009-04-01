/**
 * Testing of internals and externals
 *  
 * $Id: main.c 4134 2008-03-04 09:11:53Z elmuerte $  
 */

#include <stdio.h>

void foo()
{
}  

char* bar()
{
	return "";
}

void quux(char* str)
{
	printf("%s\n", str);
}

int main(int argc, char *argv[])
{
	foo();
	printf("%s\n", bar());
	quux("quux");
	return 0;
}
