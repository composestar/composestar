#include <string.h>

#include <stdio.h>

#include "Encryptor.h"
#define MAXLINE 1000

/********************************************
 * 
 * Encryptor veranderd alleen de waarde van de string door ieder
 * geheugen adres met 20 te verhogen, opzich wil je de parameter
 * waarde gewoon aanpassen hier, dus rechtstreeks met een pointer
 * dan zou je waarschijnlijk de string niet hoeven te kopieeren
 *  
 * *******************************/


//static int encryptor=12;

void encryptorEncrypt(struct message * mes)
{
	char* x;
	printf("encryptor Encrypt");
	x=*(char **)getArg(0, mes);
		
	while(*x != '\0'){
		*x += 20;
		x++; 
	}
	printf(" ===> %s \n",x);
}

void encryptorTextEncrypt(char* rm)
{
	char* x;
	x=rm;
	printf("Encryptor: %s", rm);
	while(*x != '\0'){
		*x+=20;	
		x++;
	}
	printf(" ===> %s \n", rm);
}

void encryptorDecrypt(struct message * mes)
{
	char* x;
	x = *(char **)getArg(0, mes);
	
	printf("Decryptor: %s", x);
	while(*x != '\0'){
		*x = *x-20;
		x++;
	}
	printf(" ===> %s \n",x);
}
