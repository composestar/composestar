using System;

using org.bouncycastle.crypto;
using org.bouncycastle.crypto.digests;
using org.bouncycastle.crypto.parameters;
using org.bouncycastle.security;

namespace org.bouncycastle.crypto.signers
{
	
	/// <summary> ISO9796-2 - mechanism using a hash function with recovery (scheme 2 and 3).
	/// <p>
	/// Note: the usual length for the salt is the length of the hash 
	/// function used in bytes.
	/// </summary>
	public class ISO9796d2PSSSigner : SignerWithRecovery
	{
		/// <summary> Return a reference to the recoveredMessage message.
		/// 
		/// </summary>
		/// <returns> the full/partial recoveredMessage message.
		/// </returns>
		/// <seealso cref="org.bouncycastle.crypto.SignerWithRecovery">
		/// </seealso>
		public byte[] getRecoveredMessage()
		{
			return recoveredMessage;
		}
		public const int TRAILER_IMPLICIT = 0xBC;
		public const int TRAILER_RIPEMD160 = 0x31CC;
		public const int TRAILER_RIPEMD128 = 0x32CC;
		public const int TRAILER_SHA1 = 0x33CC;
		
		private Digest digest;
		private AsymmetricBlockCipher cipher;
		
		private SecureRandom random;
		private byte[] standardSalt;
		
		private int hLen;
		private int trailer;
		private int keyBits;
		private byte[] block;
		private byte[] mBuf;
		private int messageLength;
		private int saltLength;
		private bool fullMessage;
		private byte[] recoveredMessage;
		
		/// <summary> Generate a signer for the with either implicit or explicit trailers
		/// for ISO9796-2, scheme 2 or 3.
		/// 
		/// </summary>
		/// <param name="cipher">base cipher to use for signature creation/verification
		/// </param>
		/// <param name="digest">digest to use.
		/// </param>
		/// <param name="saltLength">length of salt in bytes.
		/// </param>
		/// <param name="implicit">whether or not the trailer is implicit or gives the hash.
		/// </param>
		public ISO9796d2PSSSigner(AsymmetricBlockCipher cipher, Digest digest, int saltLength, bool isImplicit)
		{
			this.cipher = cipher;
			this.digest = digest;
			this.hLen = digest.getDigestSize();
			this.saltLength = saltLength;
			
			if (isImplicit)
			{
				trailer = TRAILER_IMPLICIT;
			}
			else
			{
				if (digest is SHA1Digest)
				{
					trailer = TRAILER_SHA1;
				}
				else if (digest is RIPEMD160Digest)
				{
					trailer = TRAILER_RIPEMD160;
				}
				else if (digest is RIPEMD128Digest)
				{
					trailer = TRAILER_RIPEMD128;
				}
				else
				{
					throw new System.ArgumentException("no valid trailer for digest");
				}
			}
		}
		
		/// <summary> Constructor for a signer with an explicit digest trailer.
		/// 
		/// </summary>
		/// <param name="cipher">cipher to use.
		/// </param>
		/// <param name="digest">digest to sign with.
		/// </param>
		/// <param name="saltLength">length of salt in bytes.
		/// </param>
		public ISO9796d2PSSSigner(AsymmetricBlockCipher cipher, Digest digest, int saltLength):this(cipher, digest, saltLength, false)
		{
		}


        public String getAlgorithmName()
        {
            return digest.getAlgorithmName() + "with" + "ISO9796-2S2";
        }
        
        /// <summary> Initialise the signer.
		/// 
		/// </summary>
		/// <param name="forSigning">true if for signing, false if for verification.
		/// </param>
		/// <param name="param">parameters for signature generation/verification. If the
		/// parameters are for generation they should be a ParametersWithRandom,
		/// a ParametersWithSalt, or just an RSAKeyParameters object. If RSAKeyParameters
		/// are passed in a SecureRandom will be created.
		/// </param>
		/// <exception cref="ArgumentException"> IllegalArgumentException if wrong parameter type or a fixed 
		/// salt is passed in which is the wrong length.
		/// </exception>
		public virtual void  init(bool forSigning, CipherParameters param)
		{
			RSAKeyParameters kParam = null;
			int lengthOfSalt = saltLength;
			
			if (param is ParametersWithRandom)
			{
				ParametersWithRandom p = (ParametersWithRandom) param;
				
				kParam = (RSAKeyParameters) p.getParameters();
				random = p.getRandom();
			}
			else if (param is ParametersWithSalt)
			{
				ParametersWithSalt p = (ParametersWithSalt) param;
				
				kParam = (RSAKeyParameters) p.getParameters();
				standardSalt = p.getSalt();
				lengthOfSalt = standardSalt.Length;
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
			
			keyBits = kParam.getModulus().bitLength();
			
			block = new byte[(keyBits + 7) / 8];
			
			if (trailer == TRAILER_IMPLICIT)
			{
				mBuf = new byte[block.Length - digest.getDigestSize() - lengthOfSalt - 1 - 1];
			}
			else
			{
				mBuf = new byte[block.Length - digest.getDigestSize() - lengthOfSalt - 1 - 2];
			}
			
			reset();
		}
		
		/// <summary> compare two byte arrays.</summary>
		private bool isSameAs(byte[] a, byte[] b)
		{
			if (messageLength < b.Length)
			{
				return false;
			}
			
			for (int i = 0; i != b.Length; i++)
			{
				if (a[i] != b[i])
				{
					return false;
				}
			}
			
			return true;
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
			if (messageLength < mBuf.Length)
			{
				mBuf[messageLength++] = b;
			}
			else
			{
				digest.update(b);
			}
		}
		
		/// <summary> update the internal digest with the byte array in</summary>
		public virtual void  update(byte[] inBytes, int off, int len)
		{
			while (len > 0 && messageLength < mBuf.Length)
			{
				this.update(inBytes[off]);
				off++;
				len--;
			}
			
			if (len > 0)
			{
				digest.update(inBytes, off, len);
			}
		}
		
		/// <summary> reset the internal state</summary>
		public virtual void  reset()
		{
			digest.reset();
			messageLength = 0;
			if (recoveredMessage != null)
			{
				clearBlock(recoveredMessage);
			}
			recoveredMessage = null;
			fullMessage = false;
		}
		
		/// <summary> generate a signature for the loaded message using the key we were
		/// initialised with.
		/// </summary>
		public byte[] generateSignature()
		{
			int digSize = digest.getDigestSize();
			
			
			byte[] m2Hash = new byte[digSize];
			
			digest.doFinal(m2Hash, 0);
			
			byte[] C = new byte[8];
			LtoOSP(messageLength * 8, C);
			
			digest.update(C, 0, C.Length);
			
			digest.update(mBuf, 0, messageLength);
			
			digest.update(m2Hash, 0, m2Hash.Length);
			
			byte[] salt;
			
			if (standardSalt != null)
			{
				salt = standardSalt;
			}
			else
			{
				salt = new byte[saltLength];
                random.NextBytes(salt);
			}
			
			digest.update(salt, 0, salt.Length);
			
			byte[] hash = new byte[digest.getDigestSize()];
			
			digest.doFinal(hash, 0);
			
			int tLength = 2;
			if (trailer == TRAILER_IMPLICIT)
			{
				tLength = 1;
			}
			
			int off = block.Length - messageLength - salt.Length - hLen - tLength - 1;
			
			block[off] = (byte) (0x01);
			
			Array.Copy(mBuf, 0, block, off + 1, messageLength);
			Array.Copy(salt, 0, block, off + 1 + messageLength, salt.Length);
			
			byte[] dbMask = maskGeneratorFunction1(hash, 0, hash.Length, block.Length - hLen - tLength);
			for (int i = 0; i != dbMask.Length; i++)
			{
				block[i] ^= dbMask[i];
			}
			
			Array.Copy(hash, 0, block, block.Length - hLen - tLength, hLen);
			
			if (trailer == TRAILER_IMPLICIT)
			{
				block[block.Length - 1] = (byte)TRAILER_IMPLICIT;
			}
			else
			{
				block[block.Length - 2] = (byte) ((uint)trailer >> 8);
				block[block.Length - 1] = (byte) trailer;
			}
			
			block[0] &= (byte) (0x7f);
			
			byte[] b = cipher.processBlock(block, 0, block.Length);
			
			clearBlock(mBuf);
			clearBlock(block);
			messageLength = 0;
			
			return b;
		}
		
		/// <summary> return true if the signature represents a ISO9796-2 signature
		/// for the passed in message.
		/// </summary>
		public virtual bool verifySignature(byte[] signature)
		{
			byte[] block = null;
			
			try
			{
				block = cipher.processBlock(signature, 0, signature.Length);
			}
			catch
			{
				return false;
			}
			
			int tLength = 0;
			
			if (((block[block.Length - 1] & 0xFF) ^ 0xBC) == 0)
			{
				tLength = 1;
			}
			else
			{
				int sigTrail = ((block[block.Length - 2] & 0xFF) << 8) | (block[block.Length - 1] & 0xFF);
				
				switch (sigTrail)
				{
					
					case TRAILER_RIPEMD160: 
						if (!(digest is RIPEMD160Digest))
						{
							throw new System.SystemException("signer should be initialised with RIPEMD160");
						}
						break;
					
					case TRAILER_SHA1: 
						if (!(digest is SHA1Digest))
						{
							throw new System.SystemException("signer should be initialised with SHA1");
						}
						break;
					
					case TRAILER_RIPEMD128: 
						if (!(digest is RIPEMD128Digest))
						{
							throw new System.SystemException("signer should be initialised with RIPEMD128");
						}
						break;
					
					default: 
						throw new System.ArgumentException("unrecognised hash in signature");
					
				}
				
				tLength = 2;
			}
			
			//
			// calculate H(m2)
			//
			byte[] m2Hash = new byte[hLen];
			digest.doFinal(m2Hash, 0);
			
			//
			// remove the mask
			//
			byte[] dbMask = maskGeneratorFunction1(block, block.Length - hLen - tLength, hLen, block.Length - hLen - tLength);
			for (int i = 0; i != dbMask.Length; i++)
			{
				block[i] ^= dbMask[i];
			}
			
			//
			// find out how much padding we've got
			//
			int mStart = 0;
			
			for (mStart = 0; mStart != block.Length; mStart++)
			{
				if (block[mStart] == 0x01)
				{
					break;
				}
			}
			
			mStart++;
			
			if (mStart == block.Length)
			{
				clearBlock(block);
				return false;
			}
			
			if (mStart > 1)
			{
				fullMessage = true;
			}
			else
			{
				fullMessage = false;
			}
			
			recoveredMessage = new byte[dbMask.Length - mStart - saltLength];
			
			Array.Copy(block, mStart, recoveredMessage, 0, recoveredMessage.Length);
			
			//
			// check the hashes
			//
			byte[] C = new byte[8];
			LtoOSP(recoveredMessage.Length * 8, C);
			
			digest.update(C, 0, C.Length);
			
			if (recoveredMessage.Length != 0)
			{
				digest.update(recoveredMessage, 0, recoveredMessage.Length);
			}
			
			digest.update(m2Hash, 0, m2Hash.Length);
			byte[] hash = new byte[digest.getDigestSize()];
			
			digest.update(block, mStart + recoveredMessage.Length, dbMask.Length - mStart - recoveredMessage.Length);
			
			digest.doFinal(hash, 0);
			
			int off = block.Length - tLength - hash.Length;
			
			for (int i = 0; i != hash.Length; i++)
			{
				if (hash[i] != block[off + i])
				{
					clearBlock(block);
					clearBlock(hash);
					clearBlock(recoveredMessage);
					
					return false;
				}
			}
			
			//
			// if they've input a message check what we've recovered against
			// what was input.
			//
			if (messageLength != 0)
			{
				if (!isSameAs(mBuf, recoveredMessage))
				{
					clearBlock(mBuf);
					clearBlock(block);
					clearBlock(recoveredMessage);
					
					return false;
				}
			}
			
			clearBlock(mBuf);
			clearBlock(block);
			messageLength = 0;
			
			return true;
		}
		
		/// <summary> Return true if the full message was recoveredMessage.
		/// 
		/// </summary>
		/// <returns> true on full message recovery, false otherwise, or if not sure.
		/// </returns>
		/// <seealso cref="org.bouncycastle.crypto.SignerWithRecovery">
		/// </seealso>
		public virtual bool hasFullMessage()
		{
			return fullMessage;
		}
		
		/// <summary> int to octet string.</summary>
        /// <summary> int to octet string.</summary>
        private void ItoOSP(
            int i,
            byte[] sp)
        {
            sp[0] = (byte)((uint)i >> 24);
            sp[1] = (byte)((uint)i >> 16);
            sp[2] = (byte)((uint)i >> 8);
            sp[3] = (byte)((uint)i >> 0);
        }

        /// <summary> long to octet string.</summary>
		private void  LtoOSP(long l, byte[] sp)
		{
			sp[0] = (byte)((ulong)l >> 56);
			sp[1] = (byte)((ulong)l >> 48);
			sp[2] = (byte)((ulong)l >> 40);
			sp[3] = (byte)((ulong)l >> 32);
			sp[4] = (byte)((ulong)l >> 24);
			sp[5] = (byte)((ulong)l >> 16);
			sp[6] = (byte)((ulong)l >> 8);
			sp[7] = (byte)((ulong)l >> 0);
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