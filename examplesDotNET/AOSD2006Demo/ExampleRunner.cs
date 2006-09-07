using System;

namespace EncryptionExample
{

	class ExampleRunner
	{

		public void run()
		{
			
			Client client = new Client();
			Server server = new Server();
			
			client.sendData(server,"Hallo?");

			//client.receiveData(server,"Hallo!");
		}

		static void Main(string[] args)
		{
			new ExampleRunner().run();
		}
	}
}
