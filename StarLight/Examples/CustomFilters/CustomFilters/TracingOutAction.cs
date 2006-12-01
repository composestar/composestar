using System;
using System.Collections.Generic;
using System.Text;
using System.Reflection;
using System.Collections;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Filters.FilterTypes;

namespace CustomFilters
{
    [FilterActionAttribute("TracingOutAction", FilterActionAttribute.FilterFlowBehavior.Continue,
       FilterActionAttribute.MessageSubstitutionBehavior.Original)]
    public class TracingOutAction : FilterAction
    {
        public override void Execute(JoinPointContext context)
        {
            if (context == null)
            {
                TraceBuffer.WriteLine("OUT Tracing: Context not set!");
                return;
            }


            // Sender, Target, Methodname
            String sender = "unknown";
            if (context.Sender != null) sender = context.Sender.GetType().FullName;

            String target = "unknown";
            if (context.StartTarget != null) target = context.StartTarget.GetType().FullName;

            TraceBuffer.WriteLine("OUT Tracing: Sender={0}, Target={1}, MethodName={2} ", sender, target, context.StartSelector);


            // Out arguments
            if (context.ArgumentCount > 0)
            {
                for (short i = 0; i < context.ArgumentCount; i++)
                {
                    ArgumentInfo argumentInfo = context.GetArgumentInfo(i);
                    if (argumentInfo != null && argumentInfo.IsOut())
                    {
                        if (argumentInfo.Value == null)
                        {
                            TraceBuffer.WriteLine("  argument {0} (out) -> {1} = null", i, context.GetArgumentType(i).Name);
                            continue;
                        }

                        String argvalue;
                        try
                        {
                            argvalue = argumentInfo.Value.ToString();
                        }
                        catch(Exception){
                            argvalue = "<exception>";
                        }
                        TraceBuffer.WriteLine("  argument {0} (out) -> {1} = {2}", i, context.GetArgumentType(i).Name, argvalue);
                    }
                }
            }


            // Returnvalue
            if (context.HasReturnValue)
            {
                if (context.ReturnType == null)
                {
                    TraceBuffer.WriteLine("  return type = null");
                }
                else if (context.ReturnValue == null)
                {
                    TraceBuffer.WriteLine("  return type = {0}, return value = null", context.ReturnType.FullName);
                }
                else if (context.StartSelector == "ToString")
                {
                    TraceBuffer.WriteLine("  return type = {0}, return value = <unable to determine>", context.ReturnType.FullName);
                }
                else
                {
                    String returnValue;
                    try
                    {
                        returnValue = context.ReturnValue.ToString();
                    }
                    catch (Exception)
                    {
                        returnValue = "<exception>";
                    }
                    TraceBuffer.WriteLine("  return type = {0}, return value = {1}", context.ReturnType.FullName, returnValue);
                }
            }
        }
    }
}
