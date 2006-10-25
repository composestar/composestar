using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;
using System.ComponentModel;
using System.Threading;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.ContextInfo.FilterTypes;

namespace FilterTypes
{
    [FilterActionAttribute("StartTimerAction", FilterActionAttribute.FilterFlowBehaviour.Continue, 
       FilterActionAttribute.MessageSubstitutionBehaviour.Original)]
    public class StartTimerAction : FilterAction
    {
        [DllImport("Kernel32.dll")]
        private static extern bool QueryPerformanceCounter(
            out long lpPerformanceCount);

        [DllImport("Kernel32.dll")]
        private static extern bool QueryPerformanceFrequency(
            out long lpFrequency);
        
        public override void Execute(JoinPointContext context)
        {
            long starttime = 0;
            long freq = 0;
            
            if (QueryPerformanceFrequency(out freq) == false) 
            {
                starttime = DateTime.Now.Ticks;
            }
            else 
            {
                QueryPerformanceCounter(out starttime);
            }

            context.AddProperty("frequency", freq);
            context.AddProperty("starttime", starttime);
        }
    }
}
