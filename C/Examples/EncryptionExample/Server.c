#include "Server.h"

static int server= 11;


void serverSendData(int client, char* data)
{
	printf("Server sendData: %s \n", data);
	//serverReceiveData(server,data);
}

void serverReceiveData(int server, char* data)
{
	printf("Server receiveData1: %s\n", data);
	protocolReceiveData(data);
	printf("Server receiveData2: %s\n", data);
}
