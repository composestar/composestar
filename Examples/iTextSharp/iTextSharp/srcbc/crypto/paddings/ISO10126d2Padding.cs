using System;
using org.bouncycastle.crypto;
using org.bouncycastle.security;


namespace org.bouncycastle.crypto.paddings
{

	/**
	* A padder that adds ISO10126-2 padding to a block.
	*/
	public class ISO10126d2Padding: BlockCipherPadding
	{
		SecureRandom    random;

		/**
		* Initialise the padder.
		*
		* @param random a SecureRandom if available.
		*/
		public void init(SecureRandom random)
			//throws IllegalArgumentException
		{
			if (random != null)
			{
				this.random = random;
			}
			else
			{
				this.random = new SecureRandom();
			}
		}

		/**
		* Return the name of the algorithm the cipher implements.
		*
		* @return the name of the algorithm the cipher implements.
		*/
		public String getPaddingName()
		{
			return "ISO10126-2";
		}

		/**
		* add the pad bytes to the passed in block, returning the
		* number of bytes added.
		*/
		public int addPadding(
			byte[]  inBytes,
			int     inOff)
		{
			byte code = (byte)(inBytes.Length - inOff);

			while (inOff < (inBytes.Length - 1))
			{
				inBytes[inOff] = (byte)random.nextInt();
				inOff++;
			}

			inBytes[inOff] = code;

			return code;
		}

		/**
		* return the number of pad bytes present in the block.
		*/
		public int padCount(byte[] inBytes)
			//throws InvalidCipherTextException
		{
			int count = inBytes[inBytes.Length - 1] & 0xff;

			if (count > inBytes.Length)
			{
				throw new InvalidCipherTextException("pad block corrupted");
			}

			return count;
		}
	}

}