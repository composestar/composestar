#include <stdlib.h>
#include "ExampleRunner.h"


int main(){
	char* text="aaaa";
	printf("Encryption Example send text= %s\n", text);
			
	clientSendData(text);
	return 0;
}
