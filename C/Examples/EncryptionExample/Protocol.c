#include <stdio.h>
#include <time.h>
#include <stdlib.h>
#include <string.h>
#include "Protocol.h"


void protocolSendData(char* data,char * dataTime)
{
	//Not used for system-test
	//time_t time1;
	
	//(void)time(&time1);
	sprintf(dataTime,"%i:%s",1205,data);
	protocolAddTimeStamp(dataTime);
}

void protocolAddTimeStamp(char * dataTime)
{
	printf("protocol send data: %s \n", dataTime);		
}

void protocolReceiveData(char* data)
{
	char* text; 
	printf("protocol received data: %s \n", data);
	if((text=strchr(data,':'))!=NULL)
	{
		strcpy(data,text+1);
	}	
	printf("protocol received data: %s \n", data);
}
