using System;

namespace org.bouncycastle.crypto
{
	/**
	 * base interface for general purpose byte derivation functions.
	 */
	public interface DerivationFunction
	{
		void init(DerivationParameters param);

		/**
		 * return the message digest used as the basis for the function
		 */
		Digest getDigest();

		int generateBytes(byte[] outBytes, int outOff, int len);
		//throws DataLengthException, IllegalArgumentException;
	}

}