using System;
using org.bouncycastle.crypto;
using org.bouncycastle.crypto.parameters;

namespace org.bouncycastle.crypto.modes
{
	/**
	* A Cipher Text Stealing (CTS) mode cipher. CTS allows block ciphers to
	* be used to produce cipher text which is the same length as the plain text.
	*/
	public class CTSBlockCipher : BufferedBlockCipher
	{
		private int     blockSize;

		/**
		* Create a buffered block cipher that uses Cipher Text Stealing
		*
		* @param cipher the underlying block cipher this buffering object wraps.
		*/
		public CTSBlockCipher(
			BlockCipher     cipher)
		{
			if (typeof(OFBBlockCipher).IsInstanceOfType(cipher) ||
				typeof(CFBBlockCipher).IsInstanceOfType(cipher))
			{
				throw new ArgumentException("CTSBlockCipher can only accept ECB, or CBC ciphers");
			}

			this.cipher = cipher;

			blockSize = cipher.getBlockSize();

			buf = new byte[blockSize * 2];
			bufOff = 0;
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
		* return the size of the output buffer required for an update plus a
		* doFinal with an input of len bytes.
		*
		* @param len the length of the input.
		* @return the space required to accommodate a call to update and doFinal
		* with len bytes of input.
		*/
		public override int getOutputSize(
			int len)
		{
			return len + bufOff;
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
				Array.Copy(buf, blockSize, buf, 0, blockSize);

				bufOff = blockSize;
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
				Array.Copy(buf, blockSize, buf, 0, blockSize);

				bufOff = blockSize;

				len -= gapLen;
				inOff += gapLen;

				while (len > blockSize)
				{
					Array.Copy(inBytes, inOff, buf, bufOff, blockSize);
					resultLen += cipher.processBlock(buf, 0, outBytes, outOff + resultLen);
					Array.Copy(buf, blockSize, buf, 0, blockSize);

					len -= blockSize;
					inOff += blockSize;
				}
			}

			Array.Copy(inBytes, inOff, buf, bufOff, len);

			bufOff += len;

			return resultLen;
		}

		/**
		* Process the last block in the buffer.
		*
		* @param out the array the block currently being held is copied into.
		* @param outOff the offset at which the copying starts.
		* @return the number of output bytes copied to out.
		* @exception DataLengthException if there is insufficient space in out for
		* the output.
		* @exception IllegalStateException if the underlying cipher is not
		* initialised.
		* @exception InvalidCipherTextException if cipher text decrypts wrongly (in
		* case the exception will never get thrown).
		*/
		public override int doFinal(
			byte[]  outBytes,
			int     outOff)
			//throws DataLengthException, IllegalStateException, InvalidCipherTextException
		{
			if (bufOff + outOff > outBytes.Length)
			{
				throw new DataLengthException("output buffer to small in doFinal");
			}

			int     blockSize = cipher.getBlockSize();
			int     len = bufOff - blockSize;
			byte[]  block = new byte[blockSize];

			if (forEncryption)
			{
				cipher.processBlock(buf, 0, block, 0);

				for (int i = bufOff; i != buf.Length; i++)
				{
					buf[i] = block[i - blockSize];
				}

				for (int i = blockSize; i != bufOff; i++)
				{
					buf[i] ^= block[i - blockSize];
				}
	
				if (typeof(CBCBlockCipher).IsInstanceOfType(cipher))
				{
					BlockCipher c = ((CBCBlockCipher)cipher).getUnderlyingCipher();

					c.processBlock(buf, blockSize, outBytes, outOff);
				}
				else
				{
					cipher.processBlock(buf, blockSize, outBytes, outOff);
				}

				Array.Copy(block, 0, outBytes, outOff + blockSize, len);
			}
			else
			{
				byte[]  lastBlock = new byte[blockSize];

				if (typeof(CBCBlockCipher).IsInstanceOfType(cipher))
				{
					BlockCipher c = ((CBCBlockCipher)cipher).getUnderlyingCipher();

					c.processBlock(buf, 0, block, 0);
				}
				else
				{
					cipher.processBlock(buf, 0, block, 0);
				}

				for (int i = blockSize; i != bufOff; i++)
				{
					lastBlock[i - blockSize] = (byte)(block[i - blockSize] ^ buf[i]);
				}

				Array.Copy(buf, blockSize, block, 0, len);

				cipher.processBlock(block, 0, outBytes, outOff);
				Array.Copy(lastBlock, 0, outBytes, outOff + blockSize, len);
			}

			int offset = bufOff;

			reset();

			return offset;
		}
	}

}