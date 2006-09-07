using System;
using ProtocolLibrary;

namespace EncryptionExample
{
	public class Client : CommunicationObject
	{
		SimpleTimeStampProtocol protocol = new ProtocolLibrary.SimpleTimeStampProtocol();

		public void sendData(CommunicationObject server, String data)
		{
			String tmp = protocol.sendData(data);
			Console.WriteLine("Client sendData: "+data+" ==> "+tmp);
			server.receiveData(this,tmp);
		}

		public void receiveData(CommunicationObject server, String data)
		{
			Console.WriteLine("Client receiveData: "+data);
		}
	}
}
