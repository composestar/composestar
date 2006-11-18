#include <stdio.h>
#include "message.h"
#include "Logger.h"

void loggerLog(struct message * mes)
{
	printf("Logging: [%s]\n",*(char **)getArg(0, mes));
}

void loggerSlog(struct message * mes)
{
	printf("Logging: [%s]\n",*(char **)getArg(0, mes));
}

