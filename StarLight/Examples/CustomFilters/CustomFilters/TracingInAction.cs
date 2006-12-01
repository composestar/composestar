using System;
using System.Collections.Generic;
using System.Text;
using System.Reflection;
using System.Collections;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Filters.FilterTypes;

namespace CustomFilters
{
    [FilterActionAttribute("TracingInAction", FilterActionAttribute.FilterFlowBehavior.Continue,
       FilterActionAttribute.MessageSubstitutionBehavior.Original)]
    public sealed class TracingInAction : FilterAction
    {
        public override void Execute(JoinPointContext context)
        {
            if (context == null)
            {
                TraceBuffer.WriteLine("TracingIN: Context not set!");
                return;
            }

            
            String sender = "unknown";
            if (context.Sender != null) sender = context.Sender.GetType().FullName;

            String target = "unknown";
            if (context.StartTarget != null) target = context.StartTarget.GetType().FullName;

            TraceBuffer.WriteLine("TracingIN: Sender={0}, Target={1}, Selector={2} ", sender, target, context.StartSelector);

            if (context.ArgumentCount > 0)
            {
                for (short i=0; i < context.ArgumentCount; i++)
                {
                    ArgumentInfo argumentInfo = context.GetArgumentInfo(i);
                    
                    if (argumentInfo == null)
                    {
                        TraceBuffer.WriteLine("  argument {0} -> no argument info available", i);
                        continue;
                    }

                    string argdirection = "-";
                    if ((argumentInfo.Attributes & ArgumentAttributes.In) == ArgumentAttributes.In)
                        argdirection = "input";
                    if ((argumentInfo.Attributes & ArgumentAttributes.Out) == ArgumentAttributes.Out)
                        argdirection = "output";
                    if ((argumentInfo.Attributes & ArgumentAttributes.Optional) == ArgumentAttributes.Optional)
                        argdirection = "optional";
                    
                    if (argumentInfo.Value == null)
                    {
                        TraceBuffer.WriteLine("  argument {0} ({2}) -> {1} = null", i, context.GetArgumentType(i).Name, argdirection);
                        continue;
                    }
                    String argvalue;
                    try
                    {
                        argvalue = argumentInfo.Value.ToString();
                    }
                    catch (Exception)
                    {
                        argvalue = "<exception>";
                    }
                    TraceBuffer.WriteLine("  argument {0} ({3}) -> {1} = {2}", i, context.GetArgumentType(i).Name, argvalue, argdirection);
                }
            }
        }
    }
}
