#ifndef PROTOCOL_H_
#define PROTOCOL_H_

void protocolSendData(char* data,char * dataTime);
void protocolReceiveData(char * data);
void protocolAddTimeStamp(char * dataTime);


#endif /*PROTOCOL_H_*/
