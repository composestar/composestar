/**
 * Various calculator methods
 *  
 * $Id$ 
 */

#include "calc.h"
 
int add(int x, int y)
{
	return x+y;
}

int sub(int x, int y)
{
	return x-y;
} 

int mult(int x, int y)
{
	return x*y;
}

int div(int x, int y)
{
	return x/y;
}


unsigned int nsieve(int m) {
// The Computer Language Shootout
// http://shootout.alioth.debian.org/
// Precedent C entry modified by bearophile for speed and size, 31 Jan 2006

    unsigned int count = 0, i, j;
    unsigned char * flags = (unsigned char *) malloc(m * sizeof(unsigned char));
    memset(flags, 1, m);

    for (i = 2; i < m; ++i)
        if (flags[i]) {
            ++count;
            for (j = i << 1; j < m; j += i)
                   flags[j] = 0;
    }

    free(flags);
    return count;
}
