using System;

namespace org.bouncycastle.util.encoders
{
	/// <summary>
	/// Translator interface.
	/// </summary>
	public interface Translator
	{
		int getEncodedBlockSize();

		int encode(byte[] inBytes, int inOff, int length, byte[] outBytes, int outOff);

		int getDecodedBlockSize();

		int decode(byte[] inBytes, int inOff, int length, byte[] outBytes, int outOff);
	}

}