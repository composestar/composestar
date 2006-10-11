using System;
using org.bouncycastle.math;

namespace org.bouncycastle.crypto
{
	/**
	 * The basic interface that basic Diffie-Hellman implementations
	 * conforms to.
	 */
	public interface BasicAgreement
	{
		/**
		 * initialise the agreement engine.
		 */
		void init(CipherParameters param);

		/**
		 * given a public key from a given party calculate the next
		 * message in the agreement sequence. 
		 */
		BigInteger calculateAgreement(CipherParameters pubKey);
	}

}