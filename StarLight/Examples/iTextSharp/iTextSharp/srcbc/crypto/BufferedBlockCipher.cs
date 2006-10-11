using System;

namespace org.bouncycastle.crypto
{
	/**
	* A wrapper class that allows block ciphers to be used to process data in
	* a piecemeal fashion. The BufferedBlockCipher outputs a block only when the
	* buffer is full and more data is being added, or on a doFinal.
	* <p>
	* Note: in the case where the underlying cipher is either a CFB cipher or an
	* OFB one the last block may not be a multiple of the block size.
	*/
	public class BufferedBlockCipher
	{
		protected byte[]        buf;
		protected int           bufOff;

		protected bool       forEncryption;
		protected BlockCipher   cipher;

		protected bool       partialBlockOkay;
		protected bool       pgpCFB;

		/**
		* constructor for subclasses
		*/
		protected BufferedBlockCipher()
		{
		}

		/**
		* Create a buffered block cipher without padding.
		*
		* @param cipher the underlying block cipher this buffering object wraps.
		* @param padded true if the buffer should add, or remove, pad bytes,
		* false otherwise.
		*/
		public BufferedBlockCipher(
			BlockCipher     cipher)
		{
			this.cipher = cipher;

			buf = new byte[cipher.getBlockSize()];
			bufOff = 0;

			//
			// check if we can handle partial blocks on doFinal.
			//
			String  name = cipher.getAlgorithmName();
			int     idx = name.IndexOf('/') + 1;

			pgpCFB = (idx > 0 && name.Substring(idx).StartsWith("PGP"));

			if (pgpCFB)
			{
				partialBlockOkay = true;
			}
			else
			{
				partialBlockOkay = (idx > 0 && (name.Substring(idx).StartsWith("CFB") 
												|| name.Substring(idx).StartsWith("OFB")));
			}
		}

		/**
		* return the cipher this object wraps.
		*
		* @return the cipher this object wraps.
		*/
		public virtual BlockCipher getUnderlyingCipher()
		{
			return cipher;
		}

		/**
		* initialise the cipher.
		*
		* @param forEncryption if true the cipher is initialised for
		*  encryption, if false for decryption.
		* @param param the key and other data required by the cipher.
		* @exception IllegalArgumentException if the params argument is
		* inappropriate.
		*/
		public virtual void init(
			bool             forEncryption,
			CipherParameters    parameters)
			//throws IllegalArgumentException
		{
			this.forEncryption = forEncryption;

			reset();

			cipher.init(forEncryption, parameters);
		}

		/**
		* return the blocksize for the underlying cipher.
		*
		* @return the blocksize for the underlying cipher.
		*/
		public virtual int getBlockSize()
		{
			return cipher.getBlockSize();
		}

		/**
		* return the size of the output buffer required for an update 
		* an input of len bytes.
		*
		* @param len the length of the input.
		* @return the space required to accommodate a call to update
		* with len bytes of input.
		*/
		public virtual int getUpdateOutputSize(
			int len)
		{
			int total       = len + bufOff;
			int leftOver;

			if (pgpCFB)
			{
				leftOver    = total % buf.Length - (cipher.getBlockSize() + 2);
			}
			else
			{
				leftOver    = total % buf.Length;
			}

			return total - leftOver;
		}

		/**
		* return the size of the output buffer required for an update plus a
		* doFinal with an input of len bytes.
		*
		* @param len the length of the input.
		* @return the space required to accommodate a call to update and doFinal
		* with len bytes of input.
		*/
		public virtual int getOutputSize(
			int len)
		{
			int total       = len + bufOff;
			int leftOver;

			if (pgpCFB)
			{
				leftOver    = total % buf.Length - (cipher.getBlockSize() + 2);
			}
			else
			{
				leftOver    = total % buf.Length;
				if (leftOver == 0)
				{
					return total;
				}
			}

			return total - leftOver + buf.Length;
		}

		/**
		* process a single byte, producing an output block if neccessary.
		*
		* @param in the input byte.
		* @param out the space for any output that might be produced.
		* @param outOff the offset from which the output will be copied.
		* @return the number of output bytes copied to out.
		* @exception DataLengthException if there isn't enough space in out.
		* @exception IllegalStateException if the cipher isn't initialised.
		*/
		public virtual int processByte(
			byte        inByte,
			byte[]      outBytes,
			int         outOff)
			//throws DataLengthException, IllegalStateException
		{
			int         resultLen = 0;

			buf[bufOff++] = inByte;

			if (bufOff == buf.Length)
			{
				resultLen = cipher.processBlock(buf, 0, outBytes, outOff);
				bufOff = 0;
			}

			return resultLen;
		}

		/**
		* process an array of bytes, producing output if necessary.
		*
		* @param in the input byte array.
		* @param inOff the offset at which the input data starts.
		* @param len the number of bytes to be copied out of the input array.
		* @param out the space for any output that might be produced.
		* @param outOff the offset from which the output will be copied.
		* @return the number of output bytes copied to out.
		* @exception DataLengthException if there isn't enough space in out.
		* @exception IllegalStateException if the cipher isn't initialised.
		*/
		public virtual int processBytes(
			byte[]      inBytes,
			int         inOff,
			int         len,
			byte[]      outBytes,
			int         outOff)
			//throws DataLengthException, IllegalStateException
		{
			if (len < 0)
			{
				throw new ArgumentException("Can't have a negative input length!");
			}

			int blockSize   = getBlockSize();
			int length      = getUpdateOutputSize(len);
	        
			if (length > 0)
			{
				if ((outOff + length) > outBytes.Length)
				{
					throw new DataLengthException("output buffer too short");
				}
			}

			int resultLen = 0;
			int gapLen = buf.Length - bufOff;

			if (len > gapLen)
			{
				Array.Copy(inBytes, inOff, buf, bufOff, gapLen);

				resultLen += cipher.processBlock(buf, 0, outBytes, outOff);

				bufOff = 0;
				len -= gapLen;
				inOff += gapLen;

				while (len > buf.Length)
				{
					resultLen += cipher.processBlock(inBytes, inOff, outBytes, outOff + resultLen);

					len -= blockSize;
					inOff += blockSize;
				}
			}

			Array.Copy(inBytes, inOff, buf, bufOff, len);

			bufOff += len;

			if (bufOff == buf.Length)
			{
				resultLen += cipher.processBlock(buf, 0, outBytes, outOff + resultLen);
				bufOff = 0;
			}

			return resultLen;
		}

		/**
		* Process the last block in the buffer.
		*
		* @param out the array the block currently being held is copied into.
		* @param outOff the offset at which the copying starts.
		* @return the number of output bytes copied to out.
		* @exception DataLengthException if there is insufficient space in out for
		* the output, or the input is not block size aligned and should be.
		* @exception IllegalStateException if the underlying cipher is not
		* initialised.
		* @exception InvalidCipherTextException if padding is expected and not found.
		* @exception DataLengthException if the input is not block size
		* aligned.
		*/
		public virtual int doFinal(
			byte[]  outBytes,
			int     outOff)
			//throws DataLengthException, IllegalStateException, InvalidCipherTextException
		{
			int resultLen = 0;

			if (outOff + bufOff > outBytes.Length)
			{
				throw new DataLengthException("output buffer too short for doFinal()");
			}

			if (bufOff != 0 && partialBlockOkay)
			{
				cipher.processBlock(buf, 0, buf, 0);
				resultLen = bufOff;
				bufOff = 0;
				Array.Copy(buf, 0, outBytes, outOff, resultLen);
			}
			else if (bufOff != 0)
			{
				throw new DataLengthException("data not block size aligned");
			}

			reset();

			return resultLen;
		}

		/**
		* Reset the buffer and cipher. After resetting the object is in the same
		* state as it was after the last init (if there was one).
		*/
		public virtual void reset()
		{
			//
			// clean the buffer.
			//
			for (int i = 0; i < buf.Length; i++)
			{
				buf[i] = 0;
			}

			bufOff = 0;

			//
			// reset the underlying cipher.
			//
			cipher.reset();
		}
	}

}