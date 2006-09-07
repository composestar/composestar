using System;

namespace SecretAOSDExample
{
	public class Protocol
	{
		public string sendData(String data)
		{
			/*Logger.slog(data);
			return new Encryptor().encrypt(data);	*/
			return data;
		}

		public String receiveData(String data)
		{
			/*String tmp = new Encryptor().decrypt(data);
			Logger.slog(tmp);
			return tmp;*/
			return data;
		}
	}
}