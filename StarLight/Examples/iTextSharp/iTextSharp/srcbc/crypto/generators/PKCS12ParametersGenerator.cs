using System;
using org.bouncycastle.math;
//using org.bouncycastle.security;
using org.bouncycastle.crypto;
using org.bouncycastle.crypto.parameters;
using org.bouncycastle.crypto.digests;

namespace org.bouncycastle.crypto.generators
{
	/**
	 * Generator for PBE derived keys and ivs as defined by PKCS 12 V1.0.
	 * <p>
	 * The document this implementation is based on can be found at
	 * <a href="http://www.rsasecurity.com/rsalabs/pkcs/pkcs-12/index.html">
	 * RSA's PKCS12 Page</a>
	 */
	public class PKCS12ParametersGenerator : PBEParametersGenerator
	{
		public static readonly int KEY_MATERIAL = 1;
		public static readonly int IV_MATERIAL  = 2;
		public static readonly int MAC_MATERIAL = 3;

		private Digest digest;

		private int     u;
		private int     v;

		/**
		 * Construct a PKCS 12 Parameters generator. This constructor will
		 * accept MD5, SHA1, and RIPEMD160.
		 *
		 * @param digest the digest to be used as the source of derived keys.
		 * @exception ArgumentException if an unknown digest is passed in.
		 */
		public PKCS12ParametersGenerator(
			Digest  digest)
		{
			this.digest = digest;
			if (typeof(MD5Digest).IsInstanceOfType(digest))
			{
				u = 128 / 8;
				v = 512 / 8;
			}
			else if (typeof(SHA1Digest).IsInstanceOfType(digest))
			{
				u = 160 / 8;
				v = 512 / 8;
			}
			else if (typeof(RIPEMD160Digest).IsInstanceOfType(digest))
			{
				u = 160 / 8;
				v = 512 / 8;
			}
			else
			{
				throw new ArgumentException("Digest " + digest.getAlgorithmName() + " unsupported");
			}
		}

		/**
		 * add a + b + 1, returning the result in a. The a value is treated
		 * as a BigInteger of length (b.Length * 8) bits. The result is 
		 * modulo 2^b.Length in case of overflow.
		 */
		private void adjust(
			byte[]  a,
			int     aOff,
			byte[]  b)
		{
			int  x = (b[b.Length - 1] & 0xff) + (a[aOff + b.Length - 1] & 0xff) + 1;

			a[aOff + b.Length - 1] = (byte)x;
			x = (int) ((uint) x >> 8);

			for (int i = b.Length - 2; i >= 0; i--)
			{
				x += (b[i] & 0xff) + (a[aOff + i] & 0xff);
				a[aOff + i] = (byte)x;
				x = (int) ((uint) x >> 8);
			}
		}

		/**
		 * generation of a derived key ala PKCS12 V1.0.
		 */
		private byte[] generateDerivedKey(
			int idByte,
			int n)
		{
			byte[]  D = new byte[v];
			byte[]  dKey = new byte[n];

			for (int i = 0; i != D.Length; i++)
			{
				D[i] = (byte)idByte;
			}

			byte[]  S;

			if ((salt != null) && (salt.Length != 0))
			{
				S = new byte[v * ((salt.Length + v - 1) / v)];

				for (int i = 0; i != S.Length; i++)
				{
					S[i] = salt[i % salt.Length];
				}
			}
			else
			{
				S = new byte[0];
			}

			byte[]  P;

			if ((password != null) && (password.Length != 0))
			{
				P = new byte[v * ((password.Length + v - 1) / v)];

				for (int i = 0; i != P.Length; i++)
				{
					P[i] = password[i % password.Length];
				}
			}
			else
			{
				P = new byte[0];
			}

			byte[]  I = new byte[S.Length + P.Length];

			Array.Copy(S, 0, I, 0, S.Length);
			Array.Copy(P, 0, I, S.Length, P.Length);

			byte[]  B = new byte[v];
			int     c = (n + u - 1) / u;

			for (int i = 1; i <= c; i++)
			{
				byte[]  A = new byte[u];

				digest.update(D, 0, D.Length);
				digest.update(I, 0, I.Length);
				digest.doFinal(A, 0);
				for (int j = 1; j != iterationCount; j++)
				{
					digest.update(A, 0, A.Length);
					digest.doFinal(A, 0);
				}

				for (int j = 0; j != B.Length; j++)
				{
					B[j] = A[j % A.Length];
				}

				for (int j = 0; j != I.Length / v; j++)
				{
					adjust(I, j * v, B);
				}

				if (i == c)
				{
					Array.Copy(A, 0, dKey, (i - 1) * u, dKey.Length - ((i - 1) * u));
				}
				else
				{
					Array.Copy(A, 0, dKey, (i - 1) * u, A.Length);
				}
			}

			return dKey;
		}

		/**
		 * Generate a key parameter derived from the password, salt, and iteration
		 * count we are currently initialised with.
		 *
		 * @param keySize the size of the key we want (in bits)
		 * @return a KeyParameter object.
		 */
		public override CipherParameters generateDerivedParameters(
			int keySize)
		{
			keySize = keySize / 8;

			byte[]  dKey = generateDerivedKey(KEY_MATERIAL, keySize);

			return new KeyParameter(dKey, 0, keySize);
		}

		/**
		 * Generate a key with initialisation vector parameter derived from
		 * the password, salt, and iteration count we are currently initialised
		 * with.
		 *
		 * @param keySize the size of the key we want (in bits)
		 * @param ivSize the size of the iv we want (in bits)
		 * @return a ParametersWithIV object.
		 */
		public override CipherParameters generateDerivedParameters(
			int     keySize,
			int     ivSize)
		{
			keySize = keySize / 8;
			ivSize = ivSize / 8;

			byte[]  dKey = generateDerivedKey(KEY_MATERIAL, keySize);

			byte[]  iv = generateDerivedKey(IV_MATERIAL, ivSize);

			return new ParametersWithIV(new KeyParameter(dKey, 0, keySize), iv, 0, ivSize);
		}

		/**
		 * Generate a key parameter for use with a MAC derived from the password,
		 * salt, and iteration count we are currently initialised with.
		 *
		 * @param keySize the size of the key we want (in bits)
		 * @return a KeyParameter object.
		 */
		public override CipherParameters generateDerivedMacParameters(
			int keySize)
		{
			keySize = keySize / 8;

			byte[]  dKey = generateDerivedKey(MAC_MATERIAL, keySize);

			return new KeyParameter(dKey, 0, keySize);
		}
	}

}