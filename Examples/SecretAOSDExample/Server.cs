using System;

namespace SecretAOSDExample
{
	public class Server
	{
		public void receiveData(Client client, String data)
		{
			client.receiveData(new Server(),data);
		}

		public void sendData(Client client, String data)
		{
			
		}
	}
}