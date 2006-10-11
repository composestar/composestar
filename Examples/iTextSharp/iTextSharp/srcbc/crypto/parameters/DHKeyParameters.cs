using System;
using org.bouncycastle.crypto;

namespace org.bouncycastle.crypto.parameters
{
	public class DHKeyParameters : AsymmetricKeyParameter
	{
		private DHParameters    parameters;

		protected DHKeyParameters(
			bool	       isPrivate,
			DHParameters    parameters) : base(isPrivate)
		{
			this.parameters = parameters;
		}   

		public DHParameters getParameters()
		{
			return parameters;
		}

		public override bool Equals(Object  obj)
		{
			if (!(typeof(DHKeyParameters).IsInstanceOfType(obj))) return false;
			DHKeyParameters    dhKey = (DHKeyParameters)obj;
			return (parameters != null && !parameters.Equals(dhKey.getParameters()));
		}
		public override int GetHashCode()
		{
			return base.GetHashCode();
		}
    }

    

}