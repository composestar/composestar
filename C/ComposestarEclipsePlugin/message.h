#ifndef COMPOSESTARC_MESSAGE_H
#define COMPOSESTARC_MESSAGE_H

struct message{
	char* type;
	int argumentNumber;
	void* argument[100];
};

void* getArg(int, struct message *);

#endif

