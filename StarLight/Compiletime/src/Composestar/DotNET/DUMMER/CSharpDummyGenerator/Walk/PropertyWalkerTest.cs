using System;
using System.Text;
using System.Reflection;
using System.Collections;

using DDW.CSharp.Dom;

namespace DDW.CSharp.Walk
{		
	public class PropertyWalkerTest : PropertyWalkerBase
	{
		StringBuilder sb = new StringBuilder();
		int tabSize = 2;

		public override void VisitCallback()
		{
			string name = "";
			string val = "";
			string start = "";
			if(IsProperty)
			{
				start = "[";
				name = PropertyInfo.Name;
				if(PropertyValue is IGraph)
					val = ((IGraph)PropertyValue).Text;
				else if(PropertyValue != null)
				{
					val = PropertyValue.ToString();
					if(PropertyValue is Enum)
					{
						val = Enum.GetName(PropertyValue.GetType(), PropertyValue);
					}
				}
			}
			else if(BaseElement != null)
			{
				start = "";
				name = BaseElement.GraphType.ToString();// GetType().Name;
			}
			int indent = NestingLevel * tabSize;			

			string end = (val != "") ? "]\t"+ val +"\n" : "\n";
			sb.Append(("").PadRight(indent) + start + name + end);
		}

		public override string ToString()
		{
			return sb.ToString();
		}
	}
}
