using System;
using org.bouncycastle.crypto;
using org.bouncycastle.crypto.parameters;

namespace org.bouncycastle.crypto.generators
{
	public class DESedeKeyGenerator : DESKeyGenerator
	{
		/**
			* initialise the key generator - if strength is set to zero
			* the key generated will be 192 bits in size, otherwise
			* strength can be 128 or 192 (or 112 or 168 if you don't count
			* parity bits), depending on whether you wish to do 2-key or 3-key
			* triple DES.
			*
			* @param param the parameters to be used for key generation
			*/
		public override void init(KeyGenerationParameters param)
		{
			base.init(param);
			if (strength == 0 || strength == (168 / 8))
			{
				strength = DESedeParameters.DES_EDE_KEY_LENGTH;
			}
			else if (strength == (112 / 8))
			{
				strength = 2 * DESedeParameters.DES_KEY_LENGTH;
			}
			else if (strength != DESedeParameters.DES_EDE_KEY_LENGTH
				&& strength != (2 * DESedeParameters.DES_KEY_LENGTH))
			{
				throw new ArgumentException("DESede key must be "
					+ (DESedeParameters.DES_EDE_KEY_LENGTH * 8) + " or "
					+ (2 * 8 * DESedeParameters.DES_KEY_LENGTH)
					+ " bits long.");
			}
		}

		public override byte[] generateKey()
		{
			byte[]  newKey = new byte[strength];

			do
			{
				random.nextBytes(newKey);

				DESedeParameters.setOddParity(newKey);
			}
			while (DESedeParameters.isWeakKey(newKey, 0, newKey.Length));

			return newKey;
		}
	}

}