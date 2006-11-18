#ifndef SERVER_H_
#define SERVER_H_

#include <stdio.h>
#include "Protocol.h"

void serverReceiveData(int client, char* data);
void serverSendData(int client, char* data);
		
#endif /*SERVER_H_*/
