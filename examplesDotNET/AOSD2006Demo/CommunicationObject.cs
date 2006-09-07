using System;

namespace EncryptionExample
{
	public interface CommunicationObject
	{
		void sendData(CommunicationObject co, String data);

		void receiveData(CommunicationObject co, String data);
	}
}
			