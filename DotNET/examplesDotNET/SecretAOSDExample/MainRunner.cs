using System;

namespace SecretAOSDExample
{
	public class MainRunner
	{
		public static void Main(string[] args)
		{
			Protocol proc = new Protocol();
			Client client = new Client();
			Server server = new Server();
			
			client.sentData(server,proc.sendData("Hallo?"));

			client.receiveData(server,proc.receiveData(new Encryptor().textEncrypt("Hallo!")));
		}

		// D:\examples\SecretAOSDExample\FilterOrderingSpec.xml
	}
}