using System;

namespace org.bouncycastle.crypto
{
	/**
	 * The base interface for implementations of message authentication codes (MACs).
	 */
	public interface Mac
	{
		/**
		 * Initialise the MAC.
		 *
		 * @param param the key and other data required by the MAC.
		 * @exception IllegalArgumentException if the params argument is
		 * inappropriate.
		 */
		void init(CipherParameters parameters);
		//throws IllegalArgumentException;

		/**
		 * Return the name of the algorithm the MAC implements.
		 *
		 * @return the name of the algorithm the MAC implements.
		 */
		String getAlgorithmName();

		/**
		 * Return the block size for this cipher (in bytes).
		 *
		 * @return the block size for this cipher in bytes.
		 */
		int getMacSize();

		/**
		 * add a single byte to the mac for processing.
		 *
		 * @param in the byte to be processed.
		 * @exception IllegalStateException if the MAC is not initialised.
		 */
		void update(byte inByte);
		//throws IllegalStateException;

		/**
		 * @param in the array containing the input.
		 * @param inOff the index in the array the data begins at.
		 * @param len the length of the input starting at inOff.
		 * @exception IllegalStateException if the MAC is not initialised.
		 * @exception DataLengthException if there isn't enough data in in.
		 */
		void update(byte[] inBytes, int inOff, int len);
		//throws DataLengthException, IllegalStateException;

		/**
		 * Compute the final statge of the MAC writing the output to the out
		 * parameter.
		 * <p>
		 * doFinal leaves the MAC in the same state it was after the last init.
		 *
		 * @param out the array the MAC is to be output to.
		 * @param outOff the offset into the out buffer the output is to start at.
		 * @exception DataLengthException if there isn't enough space in out.
		 * @exception IllegalStateException if the MAC is not initialised.
		 */
		int doFinal(byte[] outBytes, int outOff);
		//throws DataLengthException, IllegalStateException;

		/**
		 * Reset the MAC. At the end of resetting the MAC should be in the
		 * in the same state it was after the last init (if there was one).
		 */
		void reset();
	}

}
