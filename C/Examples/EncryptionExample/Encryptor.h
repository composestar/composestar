#ifndef ENCRYPTOR_H_
#define ENCRYPTOR_H_
#include "message.h"

void encryptorEncrypt(struct message * mes);
void encryptorTextEncrypt(char* msg);
void encryptorDecrypt(struct message * mes);

#endif /** ENCRYPTOR_H_ **/


