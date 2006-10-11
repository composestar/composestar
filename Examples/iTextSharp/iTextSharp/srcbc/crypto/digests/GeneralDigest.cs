using System;

namespace org.bouncycastle.crypto.digests
{
	/**
	* base implementation of MD4 family style digest as outlined in
	* "Handbook of Applied Cryptography", pages 344 - 347.
	*/
	public abstract class GeneralDigest : org.bouncycastle.crypto.Digest
	{
		private byte[]  xBuf;
		private int     xBufOff;

		private long    byteCount;

		protected GeneralDigest()
		{
			xBuf = new byte[4];
			xBufOff = 0;
		}

		protected GeneralDigest(GeneralDigest t)
		{
			xBuf = new byte[t.xBuf.Length];
			Array.Copy(t.xBuf, 0, xBuf, 0, t.xBuf.Length);

			xBufOff = t.xBufOff;
			byteCount = t.byteCount;
		}

		public void update(byte inbyte)
		{
			xBuf[xBufOff++] = inbyte;

			if (xBufOff == xBuf.Length)
			{
				processWord(xBuf, 0);
				xBufOff = 0;
			}

			byteCount++;
		}

		public void update(
			byte[]  inBytes,
			int     inOff,
			int     len)
		{
			//
			// fill the current word
			//
			while ((xBufOff != 0) && (len > 0))
			{
				update(inBytes[inOff]);
				inOff++;
				len--;
			}

			//
			// process whole words.
			//
			while (len > xBuf.Length)
			{
				processWord(inBytes, inOff);

				inOff += xBuf.Length;
				len -= xBuf.Length;
				byteCount += xBuf.Length;
			}

			//
			// load in the remainder.
			//
			while (len > 0)
			{
				update(inBytes[inOff]);

				inOff++;
				len--;
			}
		}

		public void finish()
		{
			long    bitLength = (byteCount << 3);

			//
			// add the pad bytes.
			//
			update((byte)128);

			while (xBufOff != 0) update((byte)0);
			processLength(bitLength);
			processBlock();
		}

		public virtual void reset()
		{
			byteCount = 0;
			xBufOff = 0;
			for ( int i = 0; i < xBuf.Length; i++ ) xBuf[i] = 0;
		}

		protected abstract void processWord(byte[] inBytes, int inOff);
		protected abstract void processLength(long bitLength);
		protected abstract void processBlock();
		public abstract String getAlgorithmName();
		public abstract int getDigestSize();
		public abstract int doFinal(byte[] outBytes, int outOff);
	}
}