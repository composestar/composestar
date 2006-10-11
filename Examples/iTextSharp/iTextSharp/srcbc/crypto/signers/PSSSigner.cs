using System;

using org.bouncycastle.security;
using org.bouncycastle.crypto;
using org.bouncycastle.crypto.parameters;

namespace org.bouncycastle.crypto.signers
{
	
	/// <summary> RSA-PSS as described in PKCS# 1 v 2.1.
	/// <p>
	/// Note: the usual value for the salt length is the number of
	/// bytes in the hash function.
	/// </summary>
	public class PSSSigner : Signer
	{
		public static byte TRAILER_IMPLICIT = (byte)0xBC;
		
		private Digest digest;
		private AsymmetricBlockCipher cipher;
		private SecureRandom random;
		
		private int hLen;
		private int sLen;
		private int emBits;
		private byte[] salt;
		private byte[] mDash;
		private byte[] block;
		
		/// <summary> basic constructor
		/// 
		/// </summary>
		/// <param name="cipher">the assymetric cipher to use.
		/// </param>
		/// <param name="digest">the digest to use.
		/// </param>
		/// <param name="sLen">the length of the salt to use (in bytes).
		/// </param>
		public PSSSigner(AsymmetricBlockCipher cipher, Digest digest, int sLen)
		{
			this.cipher = cipher;
			this.digest = digest;
			this.hLen = digest.getDigestSize();
			this.sLen = sLen;
			this.salt = new byte[sLen];
			this.mDash = new byte[8 + sLen + hLen];
		}
		
        public PSSSigner(
            AsymmetricBlockCipher cipher,
            Digest digest) : this(cipher, digest, digest.getDigestSize())
        {
        }

        public String getAlgorithmName()
        {
            return digest.getAlgorithmName() + "withRSAandMGF1";
        }
		public virtual void  init(bool forSigning, CipherParameters param)
		{
			RSAKeyParameters kParam = null;
			
			if (param is ParametersWithRandom)
			{
				ParametersWithRandom p = (ParametersWithRandom) param;
				
				kParam = (RSAKeyParameters) p.getParameters();
				random = p.getRandom();
			}
			else
			{
				kParam = (RSAKeyParameters) param;
				if (forSigning)
				{
					random = new SecureRandom();
				}
			}
			
			cipher.init(forSigning, kParam);
		
			emBits = kParam.getModulus().bitLength() - 1;
			
			block = new byte[(emBits + 7) / 8];
			
			reset();
		}
		
		/// <summary> clear possible sensitive data</summary>
		private void  clearBlock(byte[] block)
		{
			for (int i = 0; i != block.Length; i++)
			{
				block[i] = 0;
			}
		}
		
		/// <summary> update the internal digest with the byte b</summary>
		public virtual void  update(byte b)
		{
			digest.update(b);
		}
		
		/// <summary> update the internal digest with the byte array in</summary>
		public virtual void  update(byte[] in_Renamed, int off, int len)
		{
			digest.update(in_Renamed, off, len);
		}
		
		/// <summary> reset the internal state</summary>
		public virtual void  reset()
		{
			digest.reset();
		}
		
		/// <summary> generate a signature for the message we've been loaded with using
		/// the key we were initialised with.
		/// </summary>
		public virtual byte[] generateSignature()
		{
			if (emBits < (8 * hLen + 8 * sLen + 9))
			{
				throw new DataLengthException("encoding error");
			}
			
			digest.doFinal(mDash, mDash.Length - hLen - sLen);
			
			if (sLen != 0)
			{
			    random.nextBytes(salt);
				
				Array.Copy(salt, 0, mDash, mDash.Length - sLen, sLen);
			}
			
			byte[] h = new byte[hLen];
			
			digest.update(mDash, 0, mDash.Length);
			
			digest.doFinal(h, 0);
			
			block[block.Length - sLen - 1 - hLen - 1] = (byte) (0x01);
			Array.Copy(salt, 0, block, block.Length - sLen - hLen - 1, sLen);
			
			byte[] dbMask = maskGeneratorFunction1(h, 0, h.Length, block.Length - hLen - 1);
			for (int i = 0; i != dbMask.Length; i++)
			{
				block[i] ^= dbMask[i];
			}
			
			block[0] &= (byte) ((0xff >> ((block.Length * 8) - emBits)));
			
			Array.Copy(h, 0, block, block.Length - hLen - 1, hLen);
			
			block[block.Length - 1] = TRAILER_IMPLICIT;
			
			byte[] b = cipher.processBlock(block, 0, block.Length);
			
			clearBlock(block);
			
			return b;
		}
		
		/// <summary> return true if the internal state represents the signature described
		/// in the passed in array.
		/// </summary>
		public virtual bool verifySignature(byte[] signature)
		{
			if (emBits < (8 * hLen + 8 * sLen + 9))
			{
				return false;
			}
			
			digest.doFinal(mDash, mDash.Length - hLen - sLen);
			
			try
			{
				byte[] b = cipher.processBlock(signature, 0, signature.Length);
				Array.Copy(b, 0, block, block.Length - b.Length, b.Length);
			}
			catch
			{
				return false;
			}
			
			if (block[block.Length - 1] != TRAILER_IMPLICIT)
			{
				clearBlock(block);
				return false;
			}
			
			byte[] dbMask = maskGeneratorFunction1(block, block.Length - hLen - 1, hLen, block.Length - hLen - 1);
			
			for (int i = 0; i != dbMask.Length; i++)
			{
				block[i] ^= dbMask[i];
			}
			
			block[0] &= (byte) ((0xff >> ((block.Length * 8) - emBits)));
			
			for (int i = 0; i != block.Length - hLen - sLen - 2; i++)
			{
				if (block[i] != 0)
				{
					clearBlock(block);
					return false;
				}
			}
			
			if (block[block.Length - hLen - sLen - 2] != 0x01)
			{
				clearBlock(block);
				return false;
			}
			
			Array.Copy(block, block.Length - sLen - hLen - 1, mDash, mDash.Length - sLen, sLen);
			
			digest.update(mDash, 0, mDash.Length);
			digest.doFinal(mDash, mDash.Length - hLen);
			
			for (int i = block.Length - hLen - 1, j = mDash.Length - hLen; j != mDash.Length; i++, j++)
			{
				if ((block[i] ^ mDash[j]) != 0)
				{
					clearBlock(mDash);
					clearBlock(block);
					return false;
				}
			}
			
			clearBlock(mDash);
			clearBlock(block);
			
			return true;
		}
		
		/// <summary> int to octet string.</summary>
        private void ItoOSP(
            int     i,
            byte[]  sp)
        {
            sp[0] = (byte)((uint) i >> 24);
            sp[1] = (byte)((uint) i >> 16);
            sp[2] = (byte)((uint) i >> 8);
            sp[3] = (byte)((uint) i >> 0);
        }
		
		/// <summary> mask generator function, as described in PKCS1v2.</summary>
		private byte[] maskGeneratorFunction1(byte[] Z, int zOff, int zLen, int length)
		{
			byte[] mask = new byte[length];
			byte[] hashBuf = new byte[hLen];
			byte[] C = new byte[4];
			int counter = 0;
			
			digest.reset();
			
			do 
			{
				ItoOSP(counter, C);
				
				digest.update(Z, zOff, zLen);
				digest.update(C, 0, C.Length);
				digest.doFinal(hashBuf, 0);
				
				Array.Copy(hashBuf, 0, mask, counter * hLen, hLen);
			}
			while (++counter < (length / hLen));
			
			if ((counter * hLen) < length)
			{
				ItoOSP(counter, C);
				
				digest.update(Z, zOff, zLen);
				digest.update(C, 0, C.Length);
				digest.doFinal(hashBuf, 0);
				
				Array.Copy(hashBuf, 0, mask, counter * hLen, mask.Length - (counter * hLen));
			}
			
			return mask;
		}
	}
}