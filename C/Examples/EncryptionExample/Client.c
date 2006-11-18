#include "Client.h"
#include "Server.h"
#include <stdio.h>
#include <string.h>

static int client=10;

void clientSendData(char * text )
{
	
	/** create space for message**/
	char* data = text;
	char* dataTime;
	char* newMem;
	int size;
	newMem = data;
	for(size =0; *newMem != '\0'; size++){
			newMem++;
	}
		
	size++; //begint bij 0
	
	newMem = (char*)malloc(size * sizeof(char));
	
	sprintf(newMem,"%s",data);	
	data = newMem;
	dataTime = (char*) malloc((size + 10) * sizeof(char));
	protocolSendData(data,dataTime);
	serverReceiveData(client,dataTime);
	printf("Example runned end value:%s \n", dataTime);
	free(newMem);
	free(dataTime);
}

void clientReceiveData(int server, char* data)
{
	printf("Client receiveData: %s \n", data);
}
