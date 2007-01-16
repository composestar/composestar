using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Filters.FilterTypes;

namespace BasicTests
{
	[FilterActionAttribute("TimingStopAction", FilterActionAttribute.FilterFlowBehavior.Continue,
	   FilterActionAttribute.MessageSubstitutionBehavior.Original)]
	public class TimingStopAction : FilterAction
	{
		public override void Execute(JoinPointContext context)
		{
			long stoptime = DateTime.Now.Ticks;

			long starttime = (long) context.GetProperty("starttime");

			TimeSpan timeSpan = new TimeSpan(stoptime - starttime);

			Console.WriteLine("Timed method {0} (declaring type {1})", 
				context.MethodInformation.Name, context.MethodInformation.DeclaringType.FullName);
		}
	}
}
