#include "message.h"

void* getArg(int num, struct message * msg){
	return msg->argument[num];	
}

