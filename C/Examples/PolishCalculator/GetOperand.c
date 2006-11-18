#include "main.h"
#define BUFSIZE 100

int getch(void);
void unGetch(int c);

char buf[BUFSIZE];
int bufp = 0;

/* Getop: get next operator or numeric operand. */
int Getop(char s[])
{
    int i = 0;
    int c;
    int next;

    /* Skip whitespace */
    while((s[0] = c = getch()) == ' ' || c == '\t')
        ;
    s[1] = '\0';

    /* Not a number but may contain a unary minus. */
    if(!isdigit(c) && c != '.' && c != '-')
        return c;               

    if(c == '-')
    {
        next = getch();
        if(!isdigit(next) && next != '.')
        {
           return c;
        }
        c = next;
    }
    else
        c = getch();
 
    while(isdigit(s[++i] = c))
            c = getch();
    if(c == '.')                        /* Collect fraction part. */
        while(isdigit(s[++i] = c = getch()))
                        ;
    s[i] = '\0';
    if(c != EOF)
        unGetch(c);
    return NUMBER;
}

/* Getch: get a ( possibly pushed back) character. */
int getch(void)
{
     return (bufp > 0) ? buf[--bufp]: getchar();
}

/* unGetch: push character back on input. */
void unGetch(int c)
{
    if(bufp >= BUFSIZE)
        printf("\nUnGetch: too many characters\n");
    else
        buf[bufp++] = c;
}
