using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Filters.FilterTypes;   

namespace BasicTests
{
	[FilterActionAttribute("LoggingAction", FilterActionAttribute.FilterFlowBehavior.Continue, FilterActionAttribute.MessageSubstitutionBehavior.Original)]
	public class LoggingAction : FilterAction
	{
		public override void Execute(JoinPointContext context)
		{
			Console.WriteLine("Log : cur sel:{0}, cur method: {1} (declaring type {2}).", context.CurrentSelector, context.MethodInformation.Name, context.MethodInformation.DeclaringType.FullName);
			Console.WriteLine("Local variables: {0}",
			context.MethodInformation.GetMethodBody().LocalVariables.Count);
		}
	}
}
