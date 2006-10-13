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
    [FilterActionAttribute("StopTimerAction", FilterFlowBehaviour.Continue,
        MessageSubstitutionBehaviour.Original)]
    public class StopTimerAction : FilterAction
    {
        [DllImport("Kernel32.dll")]
        private static extern bool QueryPerformanceCounter(
            out long lpPerformanceCount);

        [DllImport("Kernel32.dll")]
        private static extern bool QueryPerformanceFrequency(
            out long lpFrequency);

        public override void Execute(JoinPointContext context)
        {
            long stoptime = 0;
            double executetime = 0;

            long freq = (long)context.GetProperty("frequency");

            if (freq == 0)
            {
               stoptime = DateTime.Now.Ticks;
            }
            else
            {
                QueryPerformanceCounter(out stoptime);
            }

            if (context == null)
            {
                TraceFile.WriteLine("StopTimer: Context not set!");
                return;
            }

            long starttime = (long)context.GetProperty("starttime");

            if (freq == 0)
            {
                TimeSpan executeTimeSpan = new TimeSpan(stoptime - starttime);
                executetime = (double)executeTimeSpan.Milliseconds;
            }
            else
            {
                executetime = ((double)(stoptime - starttime) / (double)freq) / 1000;
            }

            String sender = "unknown";
            if (context.Sender != null) sender = context.Sender.ToString();

            String target = "unknown";
            if (context.StartTarget != null) target = context.StartTarget.ToString();
             
            String args = "";
            if (context.ArgumentCount > 0)
            {
                for (short i = 1; i <= context.ArgumentCount; i++)
                {
                    if (context.GetArgumentType(i) != null)
                    {
                        if (args != "") args = args + ",";
                        args = args + context.GetArgumentType(i).ToString();
                    }
                }
            }

            TraceFile.WriteLine("The execution of message: {0}.{1}({2}) took {3:0.0000} msec.", target, context.StartSelector, args, executetime);
        }
    }
}
