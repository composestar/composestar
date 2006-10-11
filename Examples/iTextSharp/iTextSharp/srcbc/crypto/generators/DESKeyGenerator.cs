using System;
using org.bouncycastle.crypto;
using org.bouncycastle.crypto.parameters;

namespace org.bouncycastle.crypto.generators
{
	public class DESKeyGenerator : CipherKeyGenerator
	{
		public override  byte[] generateKey()
		{
			byte[]  newKey = new byte[DESParameters.DES_KEY_LENGTH];

			do
			{
				random.nextBytes(newKey);

				DESParameters.setOddParity(newKey);
			}
			while (DESParameters.isWeakKey(newKey, 0));

			return newKey;
		}
	}


}