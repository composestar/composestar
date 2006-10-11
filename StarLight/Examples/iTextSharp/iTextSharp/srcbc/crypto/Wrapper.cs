using System;
using org.bouncycastle.security;

namespace org.bouncycastle.crypto
{
	public interface Wrapper
	{
		void init(bool forWrapping, CipherParameters param);

		/**
		 * Return the name of the algorithm the wrapper implements.
		 *
		 * @return the name of the algorithm the wrapper implements.
		 */
		String getAlgorithmName();

		byte[] wrap(byte[] inBytes, int inOff, int inLen);

		byte[] unwrap(byte[] inBytes, int inOff, int inLen);
		//throws InvalidCipherTextException;
	}
}