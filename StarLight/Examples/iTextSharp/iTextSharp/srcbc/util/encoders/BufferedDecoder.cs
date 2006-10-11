using System;

namespace org.bouncycastle.util.encoders
{
	
	 
	
    /// <summary>
    ///  A buffering class to allow translation from one format to another to
    ///	 be done in discrete chunks.
    /// </summary>
	public class BufferedDecoder
	{
		protected byte[]        buf;
		protected int           bufOff;

		protected Translator    translator;

		/// <summary>
		/// Create a buffered Decoder.
		/// </summary>
		/// <param name="translator">The translater to use.</param>
		/// <param name="bufSize">The size of the buffer.</param>
		public BufferedDecoder(
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
        /// <param name="inByte">Data in.</param>
        /// <param name="outBytes">Byte array for the output.</param>
        /// <param name="outOff">The offset in the output byte array to start writing from.</param>
        /// <returns>The amount of output bytes.</returns>
		public int processByte(
			byte        inByte,
			byte[]      outBytes,
			int         outOff)
		{
			int         resultLen = 0;

			buf[bufOff++] = inByte;

			if (bufOff == buf.Length)
			{
				resultLen = translator.decode(buf, 0, buf.Length, outBytes, outOff);
				bufOff = 0;
			}

			return resultLen;
		}


        /// <summary>
        /// Process data from a byte array.
        /// </summary>
        /// <param name="inBytes">The input data.</param>
        /// <param name="inOff">Start position within input data array.</param>
        /// <param name="len">Amount of data to process from input data array.</param>
        /// <param name="outBytes">Array to store output.</param>
        /// <param name="outOff">Position in output array to start writing from.</param>
        /// <returns>The amount of output bytes.</returns>
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

				resultLen += translator.decode(buf, 0, buf.Length, outBytes, outOff);

				bufOff = 0;

				len -= gapLen;
				inOff += gapLen;
				outOff += resultLen;

				int chunkSize = len - (len % buf.Length);

				resultLen += translator.decode(inBytes, inOff, chunkSize, outBytes, outOff);

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