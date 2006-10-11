using System;
using org.bouncycastle.crypto;
using org.bouncycastle.security;


namespace org.bouncycastle.crypto.paddings
{
	/**
	 * Block cipher padders are expected to conform to this interface
	 */
	public interface BlockCipherPadding
	{
		/**
		 * Initialise the padder.
		 *
		 * @param param parameters, if any required.
		 */
		void init(SecureRandom random);
			//throws IllegalArgumentException;

		/**
		 * Return the name of the algorithm the cipher implements.
		 *
		 * @return the name of the algorithm the cipher implements.
		 */
		String getPaddingName();

		/**
		 * add the pad bytes to the passed in block, returning the
		 * number of bytes added.
		 */
		int addPadding(byte[] inBytes, int inOff);

		/**
		 * return the number of pad bytes present in the block.
		 * @exception InvalidCipherTextException if the padding is badly formed
		 * or invalid.
		 */
		int padCount(byte[] inBytes);
		//throws InvalidCipherTextException;
	}

}