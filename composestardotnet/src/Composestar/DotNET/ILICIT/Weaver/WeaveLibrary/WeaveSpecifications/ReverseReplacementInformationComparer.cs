using System;
using System.Collections;
using System.Xml.XPath;

namespace Weavers.WeaveSpecifications
{
	public class ReverserReplacementInformationComparer : IComparer  
	{
		int IComparer.Compare( Object x, Object y )  
		{
			ReplacementInformation ri1 = (ReplacementInformation)x;
			ReplacementInformation ri2 = (ReplacementInformation)y;

			string s1 = "[" + ri1.AssemblyName + "]" + ri1.ClassName;
			string s2 = "[" + ri2.AssemblyName + "]" + ri2.ClassName;

			return String.Compare(s2, s1);
			//return( (new CaseInsensitiveComparer()).Compare( y, x ) );
		}
	}
}
