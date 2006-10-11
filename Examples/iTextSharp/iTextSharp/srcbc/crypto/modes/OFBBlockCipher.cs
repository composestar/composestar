using System;
using org.bouncycastle.crypto;
using org.bouncycastle.crypto.parameters;

namespace org.bouncycastle.crypto.modes
{
	/**
	* implements a Output-FeedBack (OFB) mode on top of a simple cipher.
	*/
	public class OFBBlockCipher : BlockCipher
	{
		private byte[]          IV;
		private byte[]          ofbV;
		private byte[]          ofbOutV;

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
		public OFBBlockCipher(
			BlockCipher cipher,
			int         blockSize)
		{
			this.cipher = cipher;
			this.blockSize = blockSize / 8;

			this.IV = new byte[cipher.getBlockSize()];
			this.ofbV = new byte[cipher.getBlockSize()];
			this.ofbOutV = new byte[cipher.getBlockSize()];
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
			//throws IllegalArgumentException
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
		* @return the name of the underlying algorithm followed by "/OFB"
		* and the block size in bits
		*/
		public String getAlgorithmName()
		{
			return cipher.getAlgorithmName() + "/OFB" + (blockSize * 8);
		}

	    
		/**
		* return the block size we are operating at (in bytes).
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

			cipher.processBlock(ofbV, 0, ofbOutV, 0);

			//
			// XOR the ofbV with the plaintext producing the cipher text (and
			// the next input block).
			//
			for (int i = 0; i < blockSize; i++)
			{
				outBytes[outOff + i] = (byte)(ofbOutV[i] ^ inBytes[inOff + i]);
			}

			//
			// change over the input block.
			//
			Array.Copy(ofbV, blockSize, ofbV, 0, ofbV.Length - blockSize);
			Array.Copy(ofbOutV, 0, ofbV, ofbV.Length - blockSize, blockSize);

			return blockSize;
		}

		/**
		* reset the feedback vector back to the IV and reset the underlying
		* cipher.
		*/
		public void reset()
		{
			Array.Copy(IV, 0, ofbV, 0, IV.Length);

			cipher.reset();
		}
	}

}