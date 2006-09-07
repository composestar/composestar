using System;
using ProtocolLibrary;

namespace EncryptionExample
{
	public class Server : CommunicationObject
	{
		SimpleTimeStampProtocol protocol = new ProtocolLibrary.SimpleTimeStampProtocol();

		public void receiveData(CommunicationObject client, String data)
		{
			String tmp = protocol.receiveData(data);
			Console.WriteLine("Server receiveData: "+data+" ==> "+tmp);
			//client.receiveData(new Server(),protocol.receiveData(data));
		}

		public void sendData(CommunicationObject client, String data)
		{
			SimpleTimeStampProtocol protocol = new SimpleTimeStampProtocol();
			Console.WriteLine("Server sendData: "+data);
		}
	}
}
