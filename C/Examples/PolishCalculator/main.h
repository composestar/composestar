#include<stdlib.h>
#include<stdio.h>
#include<ctype.h>
#include<math.h>

#define MAXOP 100
#define NUMBER  0
#define TRUE 1
#define FALSE 0
#define OK 2
#define ERROR 3

int Getop(char s[]);
void ST_push(double val);
double ST_pop(void);
