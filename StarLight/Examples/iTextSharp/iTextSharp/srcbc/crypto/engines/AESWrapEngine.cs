using System;
using org.bouncycastle.crypto;
using org.bouncycastle.crypto.parameters;

namespace org.bouncycastle.crypto.engines
{
	/**
	* an implementation of the AES Key Wrapper from the NIST Key Wrap
	* Specification.
	* <p>
	* For further details see: <a href="http://csrc.nist.gov/encryption/kms/key-wrap.pdf">http://csrc.nist.gov/encryption/kms/key-wrap.pdf</a>.
	*/
	public class AESWrapEngine : Wrapper
	{
		private BlockCipher     engine = new AESEngine();
		private KeyParameter    param;
		private bool         forWrapping;

		private byte[]          iv = {
								(byte)0xa6, (byte)0xa6, (byte)0xa6, (byte)0xa6,
								(byte)0xa6, (byte)0xa6, (byte)0xa6, (byte)0xa6 };

		public void init(
			bool             forWrapping,
			CipherParameters    param)
		{
			this.forWrapping = forWrapping;

			if (typeof(KeyParameter).IsInstanceOfType(param))
			{
				this.param = (KeyParameter)param;
			}
			else if (typeof(ParametersWithIV).IsInstanceOfType(param))
			{
				this.iv = ((ParametersWithIV) param).getIV();
				this.param = (KeyParameter) ((ParametersWithIV) param).getParameters();
				if (this.iv.Length != 8)
				{
				throw new ArgumentException("IV not multiple of 8");
				}
			}
		}

		public String getAlgorithmName()
		{
			return "AES";
		}

		public byte[] wrap(
			byte[]  inBytes,
			int     inOff,
			int     inLen)
		{
			if (!forWrapping)
			{
				throw new Exception("not set for wrapping");
			}

			int     n = inLen / 8;

			if ((n * 8) != inLen)
			{
				throw new DataLengthException("wrap data must be a multiple of 8 bytes");
			}

			byte[]  block = new byte[inLen + iv.Length];
			byte[]  buf = new byte[8 + iv.Length];

			Array.Copy(iv, 0, block, 0, iv.Length);
			Array.Copy(inBytes, 0, block, iv.Length, inLen);

			engine.init(true, param);

			for (int j = 0; j != 6; j++)
			{
				for (int i = 1; i <= n; i++)
				{
					Array.Copy(block, 0, buf, 0, iv.Length);
					Array.Copy(block, 8 * i, buf, iv.Length, 8);
					engine.processBlock(buf, 0, buf, 0);

					int t = n * j + i;
					for (int k = 1; t != 0; k++)
					{
						byte    v = (byte)t;

						buf[iv.Length - k] ^= v;
						t = (int) ((uint)t >> 8);
					}

					Array.Copy(buf, 0, block, 0, 8);
					Array.Copy(buf, 8, block, 8 * i, 8);
				}
			}

			return block;
		}

		public byte[] unwrap(
			byte[]  inBytes,
			int     inOff,
			int     inLen)
			//throws InvalidCipherTextException
		{
			if (forWrapping)
			{
				throw new Exception("not set for unwrapping");
			}

			int     n = inLen / 8;

			if ((n * 8) != inLen)
			{
				throw new InvalidCipherTextException("unwrap data must be a multiple of 8 bytes");
			}

			byte[]  block = new byte[inLen - iv.Length];
			byte[]  a = new byte[iv.Length];
			byte[]  buf = new byte[8 + iv.Length];

			Array.Copy(inBytes, 0, a, 0, iv.Length);
			Array.Copy(inBytes, iv.Length, block, 0, inLen - iv.Length);

			engine.init(false, param);

			n = n - 1;

			for (int j = 5; j >= 0; j--)
			{
				for (int i = n; i >= 1; i--)
				{
					Array.Copy(a, 0, buf, 0, iv.Length);
					Array.Copy(block, 8 * (i - 1), buf, iv.Length, 8);

					int t = n * j + i;
					for (int k = 1; t != 0; k++)
					{
						byte    v = (byte)t;

						buf[iv.Length - k] ^= v;
						t = (int) ((uint)t >> 8);
					}

					engine.processBlock(buf, 0, buf, 0);
					Array.Copy(buf, 0, a, 0, 8);
					Array.Copy(buf, 8, block, 8 * (i - 1), 8);
				}
			}

			for (int i = 0; i != iv.Length; i++)
			{
				if (a[i] != iv[i])
				{
					throw new InvalidCipherTextException("checksum failed");
				}
			}

			return block;
		}
	}

}