using System;

namespace SecretAOSDExample
{
	public class Client
	{

		public Client() { }

		public void sentData(Server server, String data)
		{
			server.receiveData(this,data);
		}

		public void receiveData(Server server, String data)
		{
		}
	}
}