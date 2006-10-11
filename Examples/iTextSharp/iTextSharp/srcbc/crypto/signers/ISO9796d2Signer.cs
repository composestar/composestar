using System;

using org.bouncycastle.crypto;
using org.bouncycastle.crypto.digests;
using org.bouncycastle.crypto.parameters;

namespace org.bouncycastle.crypto.signers
{
	
	/// <summary> ISO9796-2 - mechanism using a hash function with recovery (scheme 1)</summary>
	public class ISO9796d2Signer : SignerWithRecovery
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
		
		private int trailer;
		private int keyBits;
		private byte[] block;
		private byte[] mBuf;
		private int messageLength;
		private bool fullMessage;
		private byte[] recoveredMessage;
		
		/// <summary> Generate a signer for the with either implicit or explicit trailers
		/// for ISO9796-2.
		/// 
		/// </summary>
		/// <param name="cipher">base cipher to use for signature creation/verification
		/// </param>
		/// <param name="digest">digest to use.
		/// </param>
		/// <param name="implicit">whether or not the trailer is implicit or gives the hash.
		/// </param>
		public ISO9796d2Signer(AsymmetricBlockCipher cipher, Digest digest, bool isImplicit)
		{
			this.cipher = cipher;
			this.digest = digest;
			
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
		public ISO9796d2Signer(AsymmetricBlockCipher cipher, Digest digest):this(cipher, digest, false)
		{
		}

        public String getAlgorithmName()
        {
            return digest.getAlgorithmName() + "with" + "ISO9796-2S1";
        }
        
        public virtual void  init(bool forSigning, CipherParameters param)
		{
			RSAKeyParameters kParam = (RSAKeyParameters) param;
			
			cipher.init(forSigning, kParam);
			
			keyBits = kParam.getModulus().bitLength();
			
			block = new byte[(keyBits + 7) / 8];
			mBuf = new byte[block.Length - 1];
			
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
		public void  update(byte b)
		{
			digest.update(b);
			
			if (messageLength < mBuf.Length)
			{
				mBuf[messageLength] = b;
			}
			
			messageLength++;
		}
		
		/// <summary> update the internal digest with the byte array in</summary>
		public void  update(byte[] in_Renamed, int off, int len)
		{
			digest.update(in_Renamed, off, len);
			
			if (messageLength < mBuf.Length)
			{
				for (int i = 0; i < len && (i + messageLength) < mBuf.Length; i++)
				{
					mBuf[messageLength + i] = in_Renamed[off + i];
				}
			}
			
			messageLength += len;
		}
		
		/// <summary> reset the internal state</summary>
		public virtual void  reset()
		{
			digest.reset();
			messageLength = 0;
			clearBlock(mBuf);
			
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
		public virtual byte[] generateSignature()
		{
			int digSize = digest.getDigestSize();
			
			int t = 0;
			int delta = 0;
			
			if (trailer == TRAILER_IMPLICIT)
			{
				t = 8;
				delta = block.Length - digSize - 1;
				digest.doFinal(block, delta);
				block[block.Length - 1] = (byte) TRAILER_IMPLICIT;
			}
			else
			{
				t = 16;
				delta = block.Length - digSize - 2;
				digest.doFinal(block, delta);
				block[block.Length - 2] = (byte) ((uint)trailer >> 8);
				block[block.Length - 1] = (byte) trailer;
			}
			
			byte header = 0;
			int x = (digSize + messageLength) * 8 + t + 4 - keyBits;
			
			if (x > 0)
			{
				int mR = messageLength - ((x + 7) / 8);
				header = (byte) (0x60);
				
				delta -= mR;
				
				Array.Copy(mBuf, 0, block, delta, mR);
			}
			else
			{
				header = (byte) (0x40);
				delta -= messageLength;
				
				Array.Copy(mBuf, 0, block, delta, messageLength);
			}
			
			if ((delta - 1) > 0)
			{
				for (int i = delta - 1; i != 0; i--)
				{
					block[i] = (byte) 0xbb;
				}
				block[delta - 1] ^= (byte) 0x01;
				block[0] = (byte) 0x0b;
				block[0] |= header;
			}
			else
			{
				block[0] = (byte) 0x0a;
				block[0] |= header;
			}
			
			byte[] b = cipher.processBlock(block, 0, block.Length);
			
			clearBlock(mBuf);
			clearBlock(block);
			
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
			
			if (((block[0] & 0xC0) ^ 0x40) != 0)
			{
				clearBlock(mBuf);
				clearBlock(block);
				
				return false;
			}
			
			if (((block[block.Length - 1] & 0xF) ^ 0xC) != 0)
			{
				clearBlock(mBuf);
				clearBlock(block);
				
				return false;
			}
			
			int delta = 0;
			
			if (((block[block.Length - 1] & 0xFF) ^ 0xBC) == 0)
			{
				delta = 1;
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
				
				delta = 2;
			}
			
			//
			// find out how much padding we've got
			//
			int mStart = 0;
			
			for (mStart = 0; mStart != block.Length; mStart++)
			{
				if (((block[mStart] & 0x0f) ^ 0x0a) == 0)
				{
					break;
				}
			}
			
			mStart++;
			
			//
			// check the hashes
			//
			byte[] hash = new byte[digest.getDigestSize()];
			
			int off = block.Length - delta - hash.Length;
			
			//
			// there must be at least one byte of message string
			//
			if ((off - mStart) <= 0)
			{
				clearBlock(mBuf);
				clearBlock(block);
				
				return false;
			}
			
			//
			// if we contain the whole message as well, check the hash of that.
			//
			if ((block[0] & 0x20) == 0)
			{
				fullMessage = true;
				
				digest.reset();
				digest.update(block, mStart, off - mStart);
				digest.doFinal(hash, 0);
				
				for (int i = 0; i != hash.Length; i++)
				{
					block[off + i] ^= hash[i];
					if (block[off + i] != 0)
					{
						clearBlock(mBuf);
						clearBlock(block);
						
						return false;
					}
				}
				
				recoveredMessage = new byte[off - mStart];
				Array.Copy(block, mStart, recoveredMessage, 0, recoveredMessage.Length);
			}
			else
			{
				fullMessage = false;
				
				digest.doFinal(hash, 0);
				
				for (int i = 0; i != hash.Length; i++)
				{
					block[off + i] ^= hash[i];
					if (block[off + i] != 0)
					{
						clearBlock(mBuf);
						clearBlock(block);
						
						return false;
					}
				}
				
				recoveredMessage = new byte[off - mStart];
				Array.Copy(block, mStart, recoveredMessage, 0, recoveredMessage.Length);
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
			
			return true;
		}
		
		/// <summary> Return true if the full message was recoveredMessage.
		/// 
		/// </summary>
		/// <returns> true on full message recovery, false otherwise.
		/// </returns>
		/// <seealso cref="org.bouncycastle.crypto.SignerWithRecovery">
		/// </seealso>
		public virtual bool hasFullMessage()
		{
			return fullMessage;
		}
	}
}