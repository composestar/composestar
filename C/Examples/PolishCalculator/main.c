#include "main.h"

int main(void)
{
    int type;
    double op2;
    char s[MAXOP];
    int flag = TRUE;
    double answer;
	
	printf("\nWelcome to the reverse polish calculator\n");
	printf("Calculating (1-2) * (4+5), requires an input of the form:\n");
	printf("1 2 - 4 5 + * (enter)\n");
	printf("The program can be terminated by using the letter: q \n");
    while((type = Getop(s)) != EOF)
    {
        switch(type)
        {
            case NUMBER:
                 ST_push(atof(s));
                break;
            case '+':
            	if(calcAdd(&answer,ST_pop(),ST_pop()))
                	ST_push(answer);
                break;
            case '*':
            	if(calcTimes(&answer,ST_pop(),ST_pop())==OK)
	                ST_push(answer);
                break;
            case '-':
            	if(calcMinus(&answer,ST_pop(),ST_pop())==OK)
	                ST_push(answer);
                break;
            case '/':
                op2 = ST_pop();
                if(calcDivide(&answer, ST_pop(), op2)==OK)
                	ST_push(answer);
                else
                   printf("\nError: division by zero!");
                break;
           case '%':
                op2 = ST_pop();
                if(calcModulo(&answer, ST_pop(), op2)==OK)
                	ST_push(answer);
                else
                   printf("\nError: modulo by zero!");
                break;     
           case '\n':
                printf("\n\t%.8g\n", ST_pop());
                break;
           case 'q':
                printf("\nQuitted the polish calculator\n ByBy please return soon\n");
                return 0;
                break;
            default:
                printf("\nError: unknown command %s.\n", s);
                return -1;
                break;
        }
    }
    return EXIT_SUCCESS;
}
