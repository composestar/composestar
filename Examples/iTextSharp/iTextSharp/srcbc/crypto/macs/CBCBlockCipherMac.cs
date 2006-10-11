using System;
using org.bouncycastle.crypto;
using org.bouncycastle.crypto.modes;
using org.bouncycastle.crypto.paddings;

namespace org.bouncycastle.crypto.macs
{
	/**
	* standard CBC Block Cipher MAC - if no padding is specified the default of
	* pad of zeroes is used.
	*/
	public class CBCBlockCipherMac : Mac
	{
		private byte[]              mac;

		private byte[]              buf;
		private int                 bufOff;
		private BlockCipher         cipher;
		private BlockCipherPadding  padding;

		private int                 macSize;

		/**
		* create a standard MAC based on a CBC block cipher. This will produce an
		* authentication code half the length of the block size of the cipher.
		*
		* @param cipher the cipher to be used as the basis of the MAC generation.
		*/
		public CBCBlockCipherMac(BlockCipher     cipher)
		: this(cipher, (cipher.getBlockSize() * 8) / 2, null)	{}

		/**
		* create a standard MAC based on a CBC block cipher. This will produce an
		* authentication code half the length of the block size of the cipher.
		*
		* @param cipher the cipher to be used as the basis of the MAC generation.
		* @param padding the padding to be used to complete the last block.
		*/
		public CBCBlockCipherMac(
			BlockCipher         cipher,
			BlockCipherPadding  padding)
		: this(cipher, (cipher.getBlockSize() * 8) / 2, padding) { }

		/**
		* create a standard MAC based on a block cipher with the size of the
		* MAC been given in bits. This class uses CBC mode as the basis for the
		* MAC generation.
		* <p>
		* Note: the size of the MAC must be at least 24 bits (FIPS Publication 81),
		* or 16 bits if being used as a data authenticator (FIPS Publication 113),
		* and in general should be less than the size of the block cipher as it reduces
		* the chance of an exhaustive attack (see Handbook of Applied Cryptography).
		*
		* @param cipher the cipher to be used as the basis of the MAC generation.
		* @param macSizeInBits the size of the MAC in bits, must be a multiple of 8.
		*/
		public CBCBlockCipherMac(
			BlockCipher     cipher,
			int             macSizeInBits)
		: this(cipher, macSizeInBits, null) { }

		/**
		* create a standard MAC based on a block cipher with the size of the
		* MAC been given in bits. This class uses CBC mode as the basis for the
		* MAC generation.
		* <p>
		* Note: the size of the MAC must be at least 24 bits (FIPS Publication 81),
		* or 16 bits if being used as a data authenticator (FIPS Publication 113),
		* and in general should be less than the size of the block cipher as it reduces
		* the chance of an exhaustive attack (see Handbook of Applied Cryptography).
		*
		* @param cipher the cipher to be used as the basis of the MAC generation.
		* @param macSizeInBits the size of the MAC in bits, must be a multiple of 8.
		* @param padding the padding to be used to complete the last block.
		*/
		public CBCBlockCipherMac(
			BlockCipher         cipher,
			int                 macSizeInBits,
			BlockCipherPadding  padding)
		{
			if ((macSizeInBits % 8) != 0)
			{
				throw new ArgumentException("MAC size must be multiple of 8");
			}

			this.cipher = new CBCBlockCipher(cipher);
			this.padding = padding;
			this.macSize = macSizeInBits / 8;

			mac = new byte[cipher.getBlockSize()];

			buf = new byte[cipher.getBlockSize()];
			bufOff = 0;
		}

		public String getAlgorithmName()
		{
			return cipher.getAlgorithmName();
		}

		public void init(
			CipherParameters    parameters)
		{
			reset();

			cipher.init(true, parameters);
		}

		public int getMacSize()
		{
			return macSize;
		}

		public void update(
			byte        inByte)
		{
			int         resultLen = 0;

			if (bufOff == buf.Length)
			{
				resultLen = cipher.processBlock(buf, 0, mac, 0);
				bufOff = 0;
			}

			buf[bufOff++] = inByte;
		}

		public void update(
			byte[]      inBytes,
			int         inOff,
			int         len)
		{
			if (len < 0)
			{
				throw new ArgumentException("Can't have a negative input length!");
			}

			int blockSize = cipher.getBlockSize();
			int resultLen = 0;
			int gapLen = blockSize - bufOff;

			if (len > gapLen)
			{
				Array.Copy(inBytes, inOff, buf, bufOff, gapLen);

				resultLen += cipher.processBlock(buf, 0, mac, 0);

				bufOff = 0;
				len -= gapLen;
				inOff += gapLen;

				while (len > blockSize)
				{
					resultLen += cipher.processBlock(inBytes, inOff, mac, 0);

					len -= blockSize;
					inOff += blockSize;
				}
			}

			Array.Copy(inBytes, inOff, buf, bufOff, len);

			bufOff += len;
		}

		public int doFinal(
			byte[]  outBytes,
			int     outOff)
		{
			int blockSize = cipher.getBlockSize();

			if (padding == null)
			{
				//
				// pad with zeroes
				//
				while (bufOff < blockSize)
				{
					buf[bufOff] = 0;
					bufOff++;
				}
			}
			else
			{
				if (bufOff == blockSize)
				{
					cipher.processBlock(buf, 0, mac, 0);
					bufOff = 0;
				}
				
				padding.addPadding(buf, bufOff);
			}

			cipher.processBlock(buf, 0, mac, 0);

			Array.Copy(mac, 0, outBytes, outOff, macSize);

			reset();

			return macSize;
		}

		/**
		* Reset the mac generator.
		*/
		public void reset()
		{
			/*
			* clean the buffer.
			*/
			for (int i = 0; i < buf.Length; i++)
			{
				buf[i] = 0;
			}

			bufOff = 0;

			/*
			* reset the underlying cipher.
			*/
			cipher.reset();
		}
	}

}