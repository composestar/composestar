using System;
using org.bouncycastle.crypto;

namespace org.bouncycastle.crypto.parameters
{
	public class ElGamalKeyParameters : AsymmetricKeyParameter
	{
		private ElGamalParameters    parameters;

		protected ElGamalKeyParameters(
			bool         isPrivate,
			ElGamalParameters    parameters) : base(isPrivate)
		{
			this.parameters = parameters;
		}   

		public ElGamalParameters getParameters()
		{
			return parameters;
		}

		public override bool Equals(
			Object  obj)
		{
			if (!(typeof(ElGamalKeyParameters).IsInstanceOfType(obj))) return false;

			ElGamalKeyParameters    dhKey = (ElGamalKeyParameters)obj;
			return (parameters != null && !parameters.Equals(dhKey.getParameters()));
		}
		public override int GetHashCode()
		{
			return base.GetHashCode();
		}

	}
}