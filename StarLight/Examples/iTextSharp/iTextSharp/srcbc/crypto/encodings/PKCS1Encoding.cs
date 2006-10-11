using System;
using org.bouncycastle.crypto;
using org.bouncycastle.crypto.parameters;
using org.bouncycastle.crypto.digests;
using org.bouncycastle.security;


namespace org.bouncycastle.crypto.encodings
{
	/**
	* this does your basic PKCS 1 v1.5 padding - whether or not you should be using this
	* depends on your application - see PKCS1 Version 2 for details.
	*/
	public class PKCS1Encoding: AsymmetricBlockCipher
	{
		private static int      HEADER_LENGTH = 10;

		private SecureRandom            random;
		private AsymmetricBlockCipher   engine;
		private bool                 forEncryption;
		private bool                 forPrivateKey;

		public PKCS1Encoding(
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
				ParametersWithRandom    rParam = (ParametersWithRandom)param;

				this.random = rParam.getRandom();
				kParam = (AsymmetricKeyParameter)rParam.getParameters();
			}
			else
			{
				this.random = new SecureRandom();
				kParam = (AsymmetricKeyParameter)param;
			}

			engine.init(forEncryption, kParam);

			this.forPrivateKey = kParam.isPrivate();
			this.forEncryption = forEncryption;
		}

		public int getInputBlockSize()
		{
			int     baseBlockSize = engine.getInputBlockSize();

			if (forEncryption)
			{
				return baseBlockSize - HEADER_LENGTH;
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
				return baseBlockSize - HEADER_LENGTH;
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

		private byte[] encodeBlock(
			byte[]  inBytes,
			int     inOff,
			int     inLen)
			//throws InvalidCipherTextException
		{
			byte[]  block = new byte[engine.getInputBlockSize()];

			if (forPrivateKey)
			{
				block[0] = 0x01;                        // type code 1

				for (int i = 1; i != block.Length - inLen - 1; i++)
				{
					block[i] = (byte)0xFF;
				}
			}
			else
			{
				random.nextBytes(block);                // random fill

				block[0] = 0x02;                        // type code 2

				//
				// a zero byte marks the end of the padding, so all
				// the pad bytes must be non-zero.
				//
				for (int i = 1; i != block.Length - inLen - 1; i++)
				{
					while (block[i] == 0)
					{
						block[i] = (byte)random.nextInt();
					}
				}
			}

			block[block.Length - inLen - 1] = 0x00;       // mark the end of the padding
			Array.Copy(inBytes, inOff, block, block.Length - inLen, inLen);

			return engine.processBlock(block, 0, block.Length);
		}

		/**
		* @exception InvalidCipherTextException if the decrypted block is not in PKCS1 format.
		*/
		private byte[] decodeBlock(
			byte[]  inBytes,
			int     inOff,
			int     inLen)
			//throws InvalidCipherTextException
		{
			byte[]  block = engine.processBlock(inBytes, inOff, inLen);

			if (block.Length < getOutputBlockSize())
			{
				throw new InvalidCipherTextException("block truncated");
			}

			if (block[0] != 1 && block[0] != 2)
			{
				throw new InvalidCipherTextException("unknown block type");
			}

			//
			// find and extract the message block.
			//
			int start;

			for (start = 1; start != block.Length; start++)
			{
				if (block[start] == 0)
				{
					break;
				}
			}

			start++;           // data should start at the next byte

			if (start >= block.Length || start < HEADER_LENGTH)
			{
				throw new InvalidCipherTextException("no data in block");
			}

			byte[]  result = new byte[block.Length - start];

			Array.Copy(block, start, result, 0, result.Length);

			return result;
		}
	}

}