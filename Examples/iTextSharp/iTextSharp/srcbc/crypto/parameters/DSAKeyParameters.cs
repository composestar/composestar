using System;
using org.bouncycastle.crypto;

namespace org.bouncycastle.crypto.parameters
{
	public class DSAKeyParameters : AsymmetricKeyParameter
	{
		private DSAParameters    parameters;

		public DSAKeyParameters(
			bool         isPrivate,
			DSAParameters   parameters) : base(isPrivate)
		{
			this.parameters = parameters;
		}   

		public DSAParameters getParameters()
		{
			return parameters;
		}
	}
}