using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.ContextInfo.FilterTypes;

namespace FilterTypes
{
    [FilterActionAttribute("StopTimerAction", FilterFlowBehaviour.Continue,
        MessageSubstitutionBehaviour.Original)]
    public class StopTimerAction : FilterAction
    {
        public override void Execute(JoinPointContext context)
        {
            long stoptime = DateTime.Now.Ticks;
            long starttime = (long)context.GetProperty("starttime");
            TimeSpan buildtime = new TimeSpan(stoptime - starttime);
            double timervalue = Convert.ToDouble(buildtime.Milliseconds);
            Console.WriteLine("The execution of message: " + context.GetProperty("target") + "." + 
                context.GetProperty("selector") + " took: " + timervalue + " msecs");
        }
    }
}
