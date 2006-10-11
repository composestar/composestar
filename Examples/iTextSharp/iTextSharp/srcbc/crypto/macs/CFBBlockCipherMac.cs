using System;
using org.bouncycastle.crypto;
using org.bouncycastle.crypto.modes;
using org.bouncycastle.crypto.paddings;
using org.bouncycastle.crypto.parameters;

namespace org.bouncycastle.crypto.macs
{
	/**
	* implements a Cipher-FeedBack (CFB) mode on top of a simple cipher.
	*/
	class MacCFBBlockCipher
	{
		private byte[]          IV;
		private byte[]          cfbV;
		private byte[]          cfbOutV;

		private int                 blockSize;
		private BlockCipher         cipher = null;
		//private bool             encrypting;

		/**
		* Basic constructor.
		*
		* @param cipher the block cipher to be used as the basis of the
		* feedback mode.
		* @param blockSize the block size in bits (note: a multiple of 8)
		*/
		public MacCFBBlockCipher(
			BlockCipher         cipher,
			int                 bitBlockSize)
		{
			this.cipher = cipher;
			this.blockSize = bitBlockSize / 8;

			this.IV = new byte[cipher.getBlockSize()];
			this.cfbV = new byte[cipher.getBlockSize()];
			this.cfbOutV = new byte[cipher.getBlockSize()];
		}

		/**
		* Initialise the cipher and, possibly, the initialisation vector (IV).
		* If an IV isn't passed as part of the parameter, the IV will be all zeros.
		* An IV which is too short is handled in FIPS compliant fashion.
		*
		* @param param the key and other data required by the cipher.
		* @exception ArgumentException if the params argument is
		* inappropriate.
		*/
		public void init(
			CipherParameters    parameters)
			//throws ArgumentException
		{
			//this.encrypting = true;
	        
			if (typeof(ParametersWithIV).IsInstanceOfType(parameters))
			{
					ParametersWithIV ivParam = (ParametersWithIV)parameters;
					byte[]      iv = ivParam.getIV();

					if (iv.Length < IV.Length)
					{
						Array.Copy(iv, 0, IV, IV.Length - iv.Length, iv.Length);
					}
					else
					{
						Array.Copy(iv, 0, IV, 0, IV.Length);
					}

					reset();

					cipher.init(true, ivParam.getParameters());
			}
			else
			{
					reset();

					cipher.init(true, parameters);
			}
		}

		/**
		* return the algorithm name and mode.
		*
		* @return the name of the underlying algorithm followed by "/CFB"
		* and the block size in bits.
		*/
		public String getAlgorithmName()
		{
			return cipher.getAlgorithmName() + "/CFB" + (blockSize * 8);
		}

		/**
		* return the block size we are operating at.
		*
		* @return the block size we are operating at (in bytes).
		*/
		public int getBlockSize()
		{
			return blockSize;
		}

		/**
		* Process one block of input from the array in and write it to
		* the out array.
		*
		* @param in the array containing the input data.
		* @param inOff offset into the in array the data starts at.
		* @param out the array the output data will be copied into.
		* @param outOff the offset into the out array the output will start at.
		* @exception DataLengthException if there isn't enough data in in, or
		* space in out.
		* @exception IllegalStateException if the cipher isn't initialised.
		* @return the number of bytes processed and produced.
		*/
		public int processBlock(
			byte[]      inBytes,
			int         inOff,
			byte[]      outBytes,
			int         outOff)
			//throws DataLengthException, IllegalStateException
		{
			if ((inOff + blockSize) > inBytes.Length)
			{
				throw new DataLengthException("input buffer too short");
			}

			if ((outOff + blockSize) > outBytes.Length)
			{
				throw new DataLengthException("output buffer too short");
			}

			cipher.processBlock(cfbV, 0, cfbOutV, 0);

			//
			// XOR the cfbV with the plaintext producing the cipher text
			//
			for (int i = 0; i < blockSize; i++)
			{
				outBytes[outOff + i] = (byte)(cfbOutV[i] ^ inBytes[inOff + i]);
			}

			//
			// change over the input block.
			//
			Array.Copy(cfbV, blockSize, cfbV, 0, cfbV.Length - blockSize);
			Array.Copy(outBytes, outOff, cfbV, cfbV.Length - blockSize, blockSize);

			return blockSize;
		}

		/**
		* reset the chaining vector back to the IV and reset the underlying
		* cipher.
		*/
		public void reset()
		{
			Array.Copy(IV, 0, cfbV, 0, IV.Length);

			cipher.reset();
		}

		public void getMacBlock(
			byte[]  mac)
		{
			cipher.processBlock(cfbV, 0, mac, 0);
		}
	}

	public class CFBBlockCipherMac : Mac
	{
		private byte[]              mac;

		private byte[]              buf;
		private int                 bufOff;
		private MacCFBBlockCipher   cipher;
		private BlockCipherPadding  padding = null;


		private int                 macSize;

		/**
		* create a standard MAC based on a CFB block cipher. This will produce an
		* authentication code half the length of the block size of the cipher, with
		* the CFB mode set to 8 bits.
		*
		* @param cipher the cipher to be used as the basis of the MAC generation.
		*/
		public CFBBlockCipherMac(
			BlockCipher     cipher)
		: this(cipher, 8, (cipher.getBlockSize() * 8) / 2, null) {}

		/**
		* create a standard MAC based on a CFB block cipher. This will produce an
		* authentication code half the length of the block size of the cipher, with
		* the CFB mode set to 8 bits.
		*
		* @param cipher the cipher to be used as the basis of the MAC generation.
		* @param padding the padding to be used.
		*/
		public CFBBlockCipherMac(
			BlockCipher         cipher,
			BlockCipherPadding  padding)
		: this(cipher, 8, (cipher.getBlockSize() * 8) / 2, padding) {}

		/**
		* create a standard MAC based on a block cipher with the size of the
		* MAC been given in bits. This class uses CFB mode as the basis for the
		* MAC generation.
		* <p>
		* Note: the size of the MAC must be at least 24 bits (FIPS Publication 81),
		* or 16 bits if being used as a data authenticator (FIPS Publication 113),
		* and in general should be less than the size of the block cipher as it reduces
		* the chance of an exhaustive attack (see Handbook of Applied Cryptography).
		*
		* @param cipher the cipher to be used as the basis of the MAC generation.
		* @param cfbBitSize the size of an output block produced by the CFB mode.
		* @param macSizeInBits the size of the MAC in bits, must be a multiple of 8.
		*/
		public CFBBlockCipherMac(
			BlockCipher         cipher,
			int                 cfbBitSize,
			int                 macSizeInBits)
		: this(cipher, cfbBitSize, macSizeInBits, null) {}

		/**
		* create a standard MAC based on a block cipher with the size of the
		* MAC been given in bits. This class uses CFB mode as the basis for the
		* MAC generation.
		* <p>
		* Note: the size of the MAC must be at least 24 bits (FIPS Publication 81),
		* or 16 bits if being used as a data authenticator (FIPS Publication 113),
		* and in general should be less than the size of the block cipher as it reduces
		* the chance of an exhaustive attack (see Handbook of Applied Cryptography).
		*
		* @param cipher the cipher to be used as the basis of the MAC generation.
		* @param cfbBitSize the size of an output block produced by the CFB mode.
		* @param macSizeInBits the size of the MAC in bits, must be a multiple of 8.
		* @param padding a padding to be used.
		*/
		public CFBBlockCipherMac(
			BlockCipher         cipher,
			int                 cfbBitSize,
			int                 macSizeInBits,
			BlockCipherPadding  padding)
		{
			if ((macSizeInBits % 8) != 0)
			{
				throw new ArgumentException("MAC size must be multiple of 8");
			}

			mac = new byte[cipher.getBlockSize()];

			this.cipher = new MacCFBBlockCipher(cipher, cfbBitSize);
			this.padding = padding;
			this.macSize = macSizeInBits / 8;

			buf = new byte[this.cipher.getBlockSize()];
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

			cipher.init(parameters);
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

			//
			// pad with zeroes
			//
			if (this.padding == null)
			{
				while (bufOff < blockSize)
				{
					buf[bufOff] = 0;
					bufOff++;
				}
			}
			else
			{
				padding.addPadding(buf, bufOff);
			}

			cipher.processBlock(buf, 0, mac, 0);

			cipher.getMacBlock(mac);

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