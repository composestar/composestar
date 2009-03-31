/**
 * Various tests for output filter rewriting
 *  
 * $Id: main.c 4134 2008-03-04 09:11:53Z elmuerte $  
 */

#include <stdio.h>

int foo(int a, int b, int c)
{
	return a+b+c;
}  

int bar(int a, int b, int c)
{
	return a-b-c;
}

void quux(int a, int b)
{
	printf("%d %d\n", a, b);
}


void t1()
{
	foo(1,2,3);
}

/*void t2()
{
	int res = foo(1,2,3);
	quux(res, 0);
}

void t3()
{
	int res;
	res = foo(1,2,3);
	quux(res, 0);
}

void t4()
{
	int res = foo(1,2,3);
	res = foo(1,2,3);
	quux(res, 0);
}

void t5()
{
	quux(foo(1,2,3), 4);
}

void t6()
{
	quux(0, foo(1,2,3));
}

void t7()
{
	quux(foo(1,2,3), foo(4,5,6));
}

void t8()
{
	if (foo(1,2,3))
	{
		quux(4,5);
	}
	else {
		quux(6,7);
	}
}

void t9()
{
	if (foo(1,2,3) > foo(4,5,6))
	{
		quux(7,8);
	}
	else {
		quux(9,10);
	}
}*/

int main(int argc, char *argv[])
{
	t1();
	/*t2();
	t3();
	t4();
	t5();
	t6();
	t7();
	t8();
	t9();*/
	return 0;
}
