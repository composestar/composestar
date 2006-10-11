using System;
using org.bouncycastle.math;
//using org.bouncycastle.security;
using org.bouncycastle.crypto;
using org.bouncycastle.crypto.parameters;
using org.bouncycastle.crypto.digests;
using org.bouncycastle.crypto.macs;

namespace org.bouncycastle.crypto.generators
{
	/**
	* Generator for PBE derived keys and ivs as defined by PKCS 5 V2.0 Scheme 2.
	* This generator uses a SHA-1 HMac as the calculation function.
	* <p>
	* The document this implementation is based on can be found at
	* <a href=http://www.rsasecurity.com/rsalabs/pkcs/pkcs-5/index.html>
	* RSA's PKCS5 Page</a>
	*/
	public class PKCS5S2ParametersGenerator : PBEParametersGenerator
	{
		private Mac    hMac = new HMac(new SHA1Digest());

		/**
		* construct a PKCS5 Scheme 2 Parameters generator.
		*/
		public PKCS5S2ParametersGenerator()
		{
		}

		private void F(
			byte[]  P,
			byte[]  S,
			int     c,
			byte[]  iBuf,
			byte[]  outBytes,
			int     outOff)
		{
			byte[]              state = new byte[hMac.getMacSize()];
			CipherParameters    param = new KeyParameter(P);

			hMac.init(param);

			if (S != null)
			{
				hMac.update(S, 0, S.Length);
			}

			hMac.update(iBuf, 0, iBuf.Length);

			hMac.doFinal(state, 0);

			Array.Copy(state, 0, outBytes, outOff, state.Length);

			for (int count = 1; count != c; count++)
			{
				hMac.init(param);
				hMac.update(state, 0, state.Length);
				hMac.doFinal(state, 0);

				for (int j = 0; j != state.Length; j++)
				{
					outBytes[outOff + j] ^= state[j];
				}
			}
		}

		private void intToOctet(
			byte[]  buf,
			int     i)
		{
			buf[0] = (byte)((uint) i >> 24);
			buf[1] = (byte)((uint) i >> 16);
			buf[2] = (byte)((uint) i >> 8);
			buf[3] = (byte)i;
		}

		private byte[] generateDerivedKey(
			int dkLen)
		{
			int     hLen = hMac.getMacSize();
			int     l = (dkLen + hLen - 1) / hLen;
			byte[]  iBuf = new byte[4];
			byte[]  outBytes = new byte[l * hLen];

			for (int i = 1; i <= l; i++)
			{
				intToOctet(iBuf, i);

				F(password, salt, iterationCount, iBuf, outBytes, (i - 1) * hLen);
			}

			return outBytes;
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

			byte[]  dKey = generateDerivedKey(keySize);

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

			byte[]  dKey = generateDerivedKey(keySize + ivSize);

			return new ParametersWithIV(new KeyParameter(dKey, 0, keySize), dKey, keySize, ivSize);
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
			return generateDerivedParameters(keySize);
		}
	}

}