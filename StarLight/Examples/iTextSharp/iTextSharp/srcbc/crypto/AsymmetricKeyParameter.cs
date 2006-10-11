using System;
using org.bouncycastle.crypto;

namespace org.bouncycastle.crypto
{
	public class AsymmetricKeyParameter : CipherParameters
	{
		bool privateKey;

		public AsymmetricKeyParameter(
			bool privateKey)
		{
			this.privateKey = privateKey;
		}

		public bool isPrivate()
		{
			return privateKey;
		}
	}

}