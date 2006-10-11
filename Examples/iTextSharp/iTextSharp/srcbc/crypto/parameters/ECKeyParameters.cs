using System;
using org.bouncycastle.crypto;

namespace org.bouncycastle.crypto.parameters
{
	public class ECKeyParameters: AsymmetricKeyParameter
	{
		ECDomainParameters parameters;

		protected ECKeyParameters(
			bool             isPrivate,
			ECDomainParameters  parameters): base(isPrivate)
		{
			this.parameters = parameters;
		}

		public ECDomainParameters getParameters()
		{
			return parameters;
		}
	}

}