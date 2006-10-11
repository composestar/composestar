using System;
using org.bouncycastle.crypto;
using org.bouncycastle.security;


namespace org.bouncycastle.crypto.paddings
{
	/**
	* A padder that adds X9.23 padding to a block - if a SecureRandom is
	* passed in random padding is assumed, otherwise padding with zeros is used.
	*/
	public class X923Padding: BlockCipherPadding
	{
		SecureRandom    random = null;

		/**
		* Initialise the padder.
		*
		* @param random a SecureRandom if one is available.
		*/
		public void init(SecureRandom random)
			//throws IllegalArgumentException
		{
			this.random = random;
		}

		/**
		* Return the name of the algorithm the cipher implements.
		*
		* @return the name of the algorithm the cipher implements.
		*/
		public String getPaddingName()
		{
			return "X9.23";
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

			while (inOff < inBytes.Length - 1)
			{
				if (random == null)
				{
					inBytes[inOff] = 0;
				}
				else
				{
					inBytes[inOff] = (byte)random.nextInt();
				}
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