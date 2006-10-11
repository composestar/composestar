using System;
using org.bouncycastle.crypto;
using org.bouncycastle.crypto.parameters;


namespace org.bouncycastle.crypto.paddings
{
	/**
	* A wrapper class that allows block ciphers to be used to process data in
	* a piecemeal fashion with padding. The PaddedBufferedBlockCipher
	* outputs a block only when the buffer is full and more data is being added,
	* or on a doFinal (unless the current block in the buffer is a pad block).
	* The default padding mechanism used is the one outlined in PKCS5/PKCS7.
	*/
	public class PaddedBufferedBlockCipher: BufferedBlockCipher
	{
		BlockCipherPadding  padding;

		/**
		* Create a buffered block cipher with the desired padding.
		*
		* @param cipher the underlying block cipher this buffering object wraps.
		* @param padding the padding type.
		*/
		public PaddedBufferedBlockCipher(
			BlockCipher         cipher,
			BlockCipherPadding  padding)
		{
			this.cipher = cipher;
			this.padding = padding;

			buf = new byte[cipher.getBlockSize()];
			bufOff = 0;
		}

		/**
		* Create a buffered block cipher PKCS7 padding
		*
		* @param cipher the underlying block cipher this buffering object wraps.
		*/
		public PaddedBufferedBlockCipher(BlockCipher     cipher)
			: this(cipher, new PKCS7Padding())	{ }

		/**
		* initialise the cipher.
		*
		* @param forEncryption if true the cipher is initialised for
		*  encryption, if false for decryption.
		* @param param the key and other data required by the cipher.
		* @exception IllegalArgumentException if the params argument is
		* inappropriate.
		*/
		public override void init(
			bool             forEncryption,
			CipherParameters    parameters)
			//throws IllegalArgumentException
		{
			this.forEncryption = forEncryption;

			reset();

			if (typeof(ParametersWithRandom).IsInstanceOfType(parameters))
			{
				ParametersWithRandom    p = (ParametersWithRandom)parameters;

				padding.init(p.getRandom());

				cipher.init(forEncryption, p.getParameters());
			}
			else
			{
				padding.init(null);

				cipher.init(forEncryption, parameters);
			}
		}

		/**
		* return the minimum size of the output buffer required for an update
		* plus a doFinal with an input of len bytes.
		*
		* @param len the length of the input.
		* @return the space required to accommodate a call to update and doFinal
		* with len bytes of input.
		*/
		public override int getOutputSize(
			int len)
		{
			int total       = len + bufOff;
			int leftOver    = total % buf.Length;

			if (leftOver == 0)
			{
				if (forEncryption)
				{
					return total + buf.Length;
				}

				return total;
			}

			return total - leftOver + buf.Length;
		}

		/**
		* return the size of the output buffer required for an update 
		* an input of len bytes.
		*
		* @param len the length of the input.
		* @return the space required to accommodate a call to update
		* with len bytes of input.
		*/
		public override int getUpdateOutputSize(
			int len)
		{
			int total       = len + bufOff;
			int leftOver    = total % buf.Length;

			if (leftOver == 0)
			{
				return total - buf.Length;
			}

			return total - leftOver;
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
		public override int processByte(
			byte        inByte,
			byte[]      outBytes,
			int         outOff)
			//throws DataLengthException, IllegalStateException
		{
			int         resultLen = 0;

			if (bufOff == buf.Length)
			{
				resultLen = cipher.processBlock(buf, 0, outBytes, outOff);
				bufOff = 0;
			}

			buf[bufOff++] = inByte;

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
		public override int processBytes(
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

			return resultLen;
		}

		/**
		* Process the last block in the buffer. If the buffer is currently
		* full and padding needs to be added a call to doFinal will produce
		* 2 * getBlockSize() bytes.
		*
		* @param out the array the block currently being held is copied into.
		* @param outOff the offset at which the copying starts.
		* @return the number of output bytes copied to out.
		* @exception DataLengthException if there is insufficient space in out for
		* the output or we are decrypting and the input is not block size aligned.
		* @exception IllegalStateException if the underlying cipher is not
		* initialised.
		* @exception InvalidCipherTextException if padding is expected and not found.
		*/
		public override int doFinal(
			byte[]  outBytes,
			int     outOff)
			//throws DataLengthException, IllegalStateException, InvalidCipherTextException
		{
			int blockSize = cipher.getBlockSize();
			int resultLen = 0;

			if (forEncryption)
			{
				if (bufOff == blockSize)
				{
					if ((outOff + 2 * blockSize) > outBytes.Length)
					{
						reset();

						throw new DataLengthException("output buffer too short");
					}

					resultLen = cipher.processBlock(buf, 0, outBytes, outOff);
					bufOff = 0;
				}

				padding.addPadding(buf, bufOff);

				resultLen += cipher.processBlock(buf, 0, outBytes, outOff + resultLen);

				reset();
			}
			else
			{
				if (bufOff == blockSize)
				{
					resultLen = cipher.processBlock(buf, 0, buf, 0);
					bufOff = 0;
				}
				else
				{
					reset();

					throw new DataLengthException("last block incomplete in decryption");
				}

				try
				{
					resultLen -= padding.padCount(buf);

					Array.Copy(buf, 0, outBytes, outOff, resultLen);
				}
				finally
				{
					reset();
				}
			}

			return resultLen;
		}
	}

}