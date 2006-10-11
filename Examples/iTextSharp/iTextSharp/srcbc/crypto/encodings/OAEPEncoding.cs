using System;
using org.bouncycastle.crypto;
using org.bouncycastle.crypto.parameters;
using org.bouncycastle.crypto.digests;
using org.bouncycastle.security;


namespace org.bouncycastle.crypto.encodings
{
	/**
	* Optimal Asymmetric Encryption Padding (OAEP) - see PKCS 1 V 2.
	*/
	public class OAEPEncoding: AsymmetricBlockCipher
	{
		/**
		* by default the encoding parameters for OAEP padding are the empty
		* string - rather than have to create a hash and perform the calculation,
		* this is the output string for a SHA1 hash when given an empty string
		*/
		private byte[]          defHash =
								{
									(byte)0xda, (byte)0x39, (byte)0xa3, (byte)0xee, (byte)0x5e, (byte)0x6b,
									(byte)0x4b, (byte)0x0d, (byte)0x32, (byte)0x55, (byte)0xbf, (byte)0xef,
									(byte)0x95, (byte)0x60, (byte)0x18, (byte)0x90, (byte)0xaf, (byte)0xd8,
									(byte)0x07, (byte)0x09
								};

		private SHA1Digest      hash = new SHA1Digest();     // if you change this - change defHash!

		private byte[]          encodingParams = null;  // the default

		private AsymmetricBlockCipher   engine;
		private SecureRandom            random;
		private bool                 forEncryption;

		public OAEPEncoding(
			AsymmetricBlockCipher   cipher)
		{
			this.engine = cipher;
		}

		public AsymmetricBlockCipher getUnderlyingCipher()
		{
			return engine;
		}

		public void init(
			bool             forEncryption,
			CipherParameters    param)
		{
			AsymmetricKeyParameter  kParam;

			if (typeof(ParametersWithRandom).IsInstanceOfType(param))
			{
				ParametersWithRandom  rParam = (ParametersWithRandom)param;

				this.random = rParam.getRandom();
				kParam = (AsymmetricKeyParameter)rParam.getParameters();
			}
			else
			{   
				this.random = new SecureRandom();
				kParam = (AsymmetricKeyParameter)param;
			}

			engine.init(forEncryption, kParam);

			this.forEncryption = forEncryption;
		}

		public int getInputBlockSize()
		{
			int     baseBlockSize = engine.getInputBlockSize();

			if (forEncryption)
			{
				return baseBlockSize - 1 - 2 * defHash.Length;
			}
			else
			{
				return baseBlockSize;
			}
		}

		public int getOutputBlockSize()
		{
			int     baseBlockSize = engine.getOutputBlockSize();

			if (forEncryption)
			{
				return baseBlockSize;
			}
			else
			{
				return baseBlockSize - 1 - 2 * defHash.Length;
			}
		}

		public byte[] processBlock(
			byte[]  inBytes,
			int     inOff,
			int     inLen)
			//throws InvalidCipherTextException
		{
			if (forEncryption)
			{
				return encodeBlock(inBytes, inOff, inLen);
			}
			else
			{
				return decodeBlock(inBytes, inOff, inLen);
			}
		}

		public byte[] encodeBlock(
			byte[]  inBytes,
			int     inOff,
			int     inLen)
			//throws InvalidCipherTextException
		{
			byte[]  block = new byte[getInputBlockSize() + 1 + 2 * defHash.Length];

			//
			// copy in the message
			//
			Array.Copy(inBytes, inOff, block, block.Length - inLen, inLen);

			//
			// add sentinel
			//
			block[block.Length - inLen - 1] = 0x01;

			//
			// as the block is already zeroed - there's no need to add PS (the >= 0 pad of 0)
			//

			//
			// add the hash of the encoding params.
			//
			if (encodingParams == null)
			{
				Array.Copy(defHash, 0, block, defHash.Length, defHash.Length);
			}
			else
			{
				throw new Exception("forget something?");
			}

			//
			// generate the seed.
			//
			byte[]  seed = new byte[defHash.Length];

			random.nextBytes(seed);

			//
			// mask the message block.
			//
			byte[]  mask = maskGeneratorFunction1(seed, 0, seed.Length, block.Length - defHash.Length);

			for (int i = defHash.Length; i != block.Length; i++)
			{
				block[i] ^= mask[i - defHash.Length];
			}

			//
			// add in the seed
			//
			Array.Copy(seed, 0, block, 0, defHash.Length);

			//
			// mask the seed.
			//
			mask = maskGeneratorFunction1(
							block, defHash.Length, block.Length - defHash.Length, defHash.Length);

			for (int i = 0; i != defHash.Length; i++)
			{
				block[i] ^= mask[i];
			}

			return engine.processBlock(block, 0, block.Length);
		}

		/**
		* @exception InvalidCipherTextException if the decryypted block turns out to
		* be badly formatted.
		*/
		public byte[] decodeBlock(
			byte[]  inBytes,
			int     inOff,
			int     inLen)
			//throws InvalidCipherTextException
		{
			byte[]  data = engine.processBlock(inBytes, inOff, inLen);
			byte[]  block = null;

			//
			// as we may have zeros in our leading bytes for the block we produced
			// on encryption, we need to make sure our decrypted block comes back
			// the same size.
			//
			if (data.Length < engine.getOutputBlockSize())
			{
				block = new byte[engine.getOutputBlockSize()];

				Array.Copy(data, 0, block, block.Length - data.Length, data.Length);
			}
			else
			{
				block = data;
			}

			if (block.Length < (2 * defHash.Length) + 1)
			{
				throw new InvalidCipherTextException("data too short");
			}

			//
			// unmask the seed.
			//
			byte[] mask = maskGeneratorFunction1(
						block, defHash.Length, block.Length - defHash.Length, defHash.Length);

			for (int i = 0; i != defHash.Length; i++)
			{
				block[i] ^= mask[i];
			}

			//
			// unmask the message block.
			//
			mask = maskGeneratorFunction1(block, 0, defHash.Length, block.Length - defHash.Length);

			for (int i = defHash.Length; i != block.Length; i++)
			{
				block[i] ^= mask[i - defHash.Length];
			}

			//
			// check the hash of the encoding params.
			//
			if (encodingParams == null)
			{
				for (int i = 0; i != defHash.Length; i++)
				{
					if (defHash[i] != block[defHash.Length + i])
					{
						throw new InvalidCipherTextException("data hash wrong");
					}
				}
			}
			else
			{
				throw new Exception("forget something?");
			}

			//
			// find the data block
			//
			int start;

			for (start = 2 * defHash.Length; start != block.Length; start++)
			{
				if (block[start] == 1 || block[start] != 0)
				{
					break;
				}
			}

			if (start >= (block.Length - 1) || block[start] != 1)
			{
				throw new InvalidCipherTextException("data start wrong " + start);
			}

			start++;

			//
			// extract the data block
			//
			byte[]  output = new byte[block.Length - start];

			Array.Copy(block, start, output, 0, output.Length);

			return output;
		}

		/**
		* int to octet string.
		*/
		private void ItoOSP(
			int     i,
			byte[]  sp)
		{
			sp[0] = (byte)((uint) i >> 24);
			sp[1] = (byte)((uint) i >> 16);
			sp[2] = (byte)((uint) i >> 8);
			sp[3] = (byte)((uint) i >> 0);
		}

		/**
		* mask generator function, as described in PKCS1v2.
		*/
		private byte[] maskGeneratorFunction1(
			byte[]  Z,
			int     zOff,
			int     zLen,
			int     length)
		{
			byte[]  mask = new byte[length];
			byte[]  hashBuf = new byte[defHash.Length];
			byte[]  C = new byte[4];
			int     counter = 0;

			hash.reset();

			do
			{
				ItoOSP(counter, C);

				hash.update(Z, zOff, zLen);
				hash.update(C, 0, C.Length);
				hash.doFinal(hashBuf, 0);

				Array.Copy(hashBuf, 0, mask, counter * defHash.Length, defHash.Length);
			}
			while (++counter < (length / defHash.Length));

			if ((counter * defHash.Length) < length)
			{
				ItoOSP(counter, C);

				hash.update(Z, zOff, zLen);
				hash.update(C, 0, C.Length);
				hash.doFinal(hashBuf, 0);

				Array.Copy(hashBuf, 0, mask, counter * defHash.Length, mask.Length - (counter * defHash.Length));
			}

			return mask;
		}
	}

}