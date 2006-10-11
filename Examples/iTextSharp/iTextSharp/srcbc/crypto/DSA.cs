using System;
using org.bouncycastle.math;

namespace org.bouncycastle.crypto
{
	/**
	 * interface for classes implementing the Digital Signature Algorithm
	 */
	public interface DSA
	{
		/**
		 * initialise the signer for signature generation or signature
		 * verification.
		 *
		 * @param forSigning true if we are generating a signature, false
		 * otherwise.
		 * @param param key parameters for signature generation.
		 */
		void init(bool forSigning, CipherParameters param);

		/**
		 * sign the passed in message (usually the output of a hash function).
		 *
		 * @param message the message to be signed.
		 * @return two big integers representing the r and s values respectively.
		 */
		BigInteger[] generateSignature(byte[] message);

		/**
		 * verify the message message against the signature values r and s.
		 *
		 * @param message the message that was supposed to have been signed.
		 * @param r the r signature value.
		 * @param s the s signature value.
		 */
		bool verifySignature(byte[] message, BigInteger  r, BigInteger s);
	}
}