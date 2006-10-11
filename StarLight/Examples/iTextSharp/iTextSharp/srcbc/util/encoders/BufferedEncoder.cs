using System;

namespace org.bouncycastle.util.encoders
{
	/// <summary>
	/// A class that allows encoding of data using a specific encoder to be processed in chunks.
	/// </summary>
	public class BufferedEncoder
	{
		protected byte[]        buf;
		protected int           bufOff;

		protected Translator    translator;

	
        /// <summary>
        /// Create.
        /// </summary>
        /// <param name="translator">The translator to use.</param>
        /// <param name="bufSize">Size of the chunks.</param>
        public BufferedEncoder(
			Translator  translator,
			int         bufSize)
		{
			this.translator = translator;

			if ((bufSize % translator.getEncodedBlockSize()) != 0)
			{
				throw new ArgumentException("buffer size not multiple of input block size");
			}

			buf = new byte[bufSize];
			bufOff = 0;
		}


        /// <summary>
        /// Process one byte of data.
        /// </summary>
        /// <param name="inByte">The byte.</param>
        /// <param name="outBytes">An array to store output in.</param>
        /// <param name="outOff">Offset within output array to start writing from.</param>
        /// <returns></returns>
		public int processByte(
			byte        inByte,
			byte[]      outBytes,
			int         outOff)
		{
			int         resultLen = 0;

			buf[bufOff++] = inByte;

			if (bufOff == buf.Length)
			{
				resultLen = translator.encode(buf, 0, buf.Length, outBytes, outOff);
				bufOff = 0;
			}

			return resultLen;
		}

        /// <summary>
        /// Process data from a byte array.
        /// </summary>
        /// <param name="inBytes">Input data Byte array containing data to be processed.</param>
        /// <param name="inOff">Start position within input data array.</param>
        /// <param name="len">Amount of input data to be processed.</param>
        /// <param name="outBytes">Output data array.</param>
        /// <param name="outOff">Offset within output data array to start writing to.</param>
        /// <returns>The amount of data written.</returns>
		public int processBytes(
			byte[]      inBytes,
			int         inOff,
			int         len,
			byte[]      outBytes,
			int         outOff)
		{
			if (len < 0)
			{
			throw new ArgumentException("Can't have a negative input length!");
			}

			int resultLen = 0;
			int gapLen = buf.Length - bufOff;

			if (len > gapLen)
			{
				Array.Copy(inBytes, inOff, buf, bufOff, gapLen);

				resultLen += translator.encode(buf, 0, buf.Length, outBytes, outOff);

				bufOff = 0;

				len -= gapLen;
				inOff += gapLen;
				outOff += resultLen;

				int chunkSize = len - (len % buf.Length);

				resultLen += translator.encode(inBytes, inOff, chunkSize, outBytes, outOff);

				len -= chunkSize;
				inOff += chunkSize;
			}

			if (len != 0)
			{
				Array.Copy(inBytes, inOff, buf, bufOff, len);

				bufOff += len;
			}

			return resultLen;
		}
	}

}