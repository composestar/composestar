using System;
using org.bouncycastle.crypto;

namespace org.bouncycastle.crypto.parameters
{
	public class ParametersWithIV: CipherParameters
	{
		private byte[]              iv;
		private CipherParameters    parameters;

		public ParametersWithIV(
			CipherParameters    parameters,
			byte[]              iv) 
		: this(parameters, iv, 0, iv.Length) {}

		public ParametersWithIV(
			CipherParameters    parameters,
			byte[]              iv,
			int                 ivOff,
			int                 ivLen)
		{
			this.iv = new byte[ivLen];
			this.parameters = parameters;

			Array.Copy(iv, ivOff, this.iv, 0, ivLen);
		}

		public byte[] getIV()
		{
			byte[]  tmp = new byte[iv.Length];

			Array.Copy(iv, 0, tmp, 0, tmp.Length);

			return iv;
		}

		public CipherParameters getParameters()
		{
			return parameters;
		}
	}

}