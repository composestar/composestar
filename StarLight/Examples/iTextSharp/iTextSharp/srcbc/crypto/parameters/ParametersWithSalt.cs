using System;

using org.bouncycastle.crypto;

namespace org.bouncycastle.crypto.parameters
{
	
	/// <summary> Cipher parameters with a fixed salt value associated with them.</summary>
	public class ParametersWithSalt : CipherParameters
	{
		private byte[] salt;
		private CipherParameters parameters;
		
		public ParametersWithSalt(CipherParameters parameters, byte[] salt):this(parameters, salt, 0, salt.Length)
		{
		}
		
		public ParametersWithSalt(CipherParameters parameters, byte[] salt, int saltOff, int saltLen)
		{
			this.salt = new byte[saltLen];
			this.parameters = parameters;
			
			Array.Copy(salt, saltOff, this.salt, 0, saltLen);
		}

        public byte[] getSalt()
        {
            return salt;
        }

        public CipherParameters getParameters()
        {
            return parameters;
        }
    }
}