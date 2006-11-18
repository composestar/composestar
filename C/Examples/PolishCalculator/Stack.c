#include "main.h"

#define MAXVAL 100

double val[MAXVAL];  /* value stack. */
int sp = 0;

/* push: push f onto stack. */
void ST_push(double f)
{
     val[sp++] = f;
}

/*pop: pop and return top value from stack.*/
double ST_pop(void)
{
    return val[--sp];
}
