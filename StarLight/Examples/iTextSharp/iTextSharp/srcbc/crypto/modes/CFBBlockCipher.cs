using System;
using org.bouncycastle.crypto;
using org.bouncycastle.crypto.parameters;

namespace org.bouncycastle.crypto.modes
{
	/**
	* implements a Cipher-FeedBack (CFB) mode on top of a simple cipher.
	*/
	public class CFBBlockCipher: BlockCipher
	{
		private byte[]          IV;
		private byte[]          cfbV;
		private byte[]          cfbOutV;

		private int             blockSize;
		private BlockCipher     cipher = null;
		private bool         encrypting;

		/**
		* Basic constructor.
		*
		* @param cipher the block cipher to be used as the basis of the
		* feedback mode.
		* @param blockSize the block size in bits (note: a multiple of 8)
		*/
		public CFBBlockCipher(
			BlockCipher cipher,
			int         bitBlockSize)
		{
			this.cipher = cipher;
			this.blockSize = bitBlockSize / 8;

			this.IV = new byte[cipher.getBlockSize()];
			this.cfbV = new byte[cipher.getBlockSize()];
			this.cfbOutV = new byte[cipher.getBlockSize()];
		}

		/**
		* return the underlying block cipher that we are wrapping.
		*
		* @return the underlying block cipher that we are wrapping.
		*/
		public BlockCipher getUnderlyingCipher()
		{
			return cipher;
		}

		/**
		* Initialise the cipher and, possibly, the initialisation vector (IV).
		* If an IV isn't passed as part of the parameter, the IV will be all zeros.
		* An IV which is too short is handled in FIPS compliant fashion.
		*
		* @param forEncryption if true the cipher is initialised for
		*  encryption, if false for decryption.
		* @param param the key and other data required by the cipher.
		* @exception IllegalArgumentException if the params argument is
		* inappropriate.
		*/
		public void init(
			bool             encrypting,
			CipherParameters    parameters)
			//throws ArgumentException
		{
			this.encrypting = encrypting;
	        
			if (typeof(ParametersWithIV).IsInstanceOfType(parameters))
			{
					ParametersWithIV ivParam = (ParametersWithIV)parameters;
					byte[]      iv = ivParam.getIV();

					if (iv.Length < IV.Length)
					{
						// prepend the supplied IV with zeros (per FIPS PUB 81)
						Array.Copy(iv, 0, IV, IV.Length - iv.Length, iv.Length);
						for (int i = 0; i < IV.Length - iv.Length; i++)
						{
							IV[i] = 0;
						}
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
			//throws DataLengthException, StateException
		{
			return (encrypting) ? encryptBlock(inBytes, inOff, outBytes, outOff) 
								: decryptBlock(inBytes, inOff, outBytes, outOff);
		}

		/**
		* Do the appropriate processing for CFB mode encryption.
		*
		* @param in the array containing the data to be encrypted.
		* @param inOff offset into the in array the data starts at.
		* @param out the array the encrypted data will be copied into.
		* @param outOff the offset into the out array the output will start at.
		* @exception DataLengthException if there isn't enough data in in, or
		* space in out.
		* @exception IllegalStateException if the cipher isn't initialised.
		* @return the number of bytes processed and produced.
		*/
		public int encryptBlock(
			byte[]      inBytes,
			int         inOff,
			byte[]      outBytes,
			int         outOff)
			//throws DataLengthException, StateException
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
		* Do the appropriate processing for CFB mode decryption.
		*
		* @param in the array containing the data to be decrypted.
		* @param inOff offset into the in array the data starts at.
		* @param out the array the encrypted data will be copied into.
		* @param outOff the offset into the out array the output will start at.
		* @exception DataLengthException if there isn't enough data in in, or
		* space in out.
		* @exception IllegalStateException if the cipher isn't initialised.
		* @return the number of bytes processed and produced.
		*/
		public int decryptBlock(
			byte[]      inBytes,
			int         inOff,
			byte[]      outBytes,
			int         outOff)
			//throws DataLengthException, StateException
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
			// change over the input block.
			//
			Array.Copy(cfbV, blockSize, cfbV, 0, cfbV.Length - blockSize);
			Array.Copy(inBytes, inOff, cfbV, cfbV.Length - blockSize, blockSize);

			//
			// XOR the cfbV with the plaintext producing the plain text
			//
			for (int i = 0; i < blockSize; i++)
			{
				outBytes[outOff + i] = (byte)(cfbOutV[i] ^ inBytes[inOff + i]);
			}

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
	}

}