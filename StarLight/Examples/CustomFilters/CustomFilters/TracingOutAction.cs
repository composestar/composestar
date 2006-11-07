using System;
using System.Collections.Generic;
using System.Text;
using System.Reflection;
using System.Collections;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Filters.FilterTypes;

namespace CustomFilters
{
    [FilterActionAttribute("TracingOutAction", FilterActionAttribute.FilterFlowBehaviour.Continue,
       FilterActionAttribute.MessageSubstitutionBehaviour.Original)]
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
                for (short i = 1; i <= context.ArgumentCount; i++)
                {
                    ArgumentInfo argumentInfo = context.GetArgumentInfo(i);
                    if (argumentInfo.IsOut())
                    {
                        if (argumentInfo.Value == null)
                        {
                            TraceBuffer.WriteLine("  argument {0} (out) -> {1} = null", i, context.GetArgumentType(i));
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
                        TraceBuffer.WriteLine("  argument {0} (out) -> {1} = {2}", i, context.GetArgumentType(i).FullName, argvalue);
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
                else if (context.StartSelector != "ToString")
                {
                    TraceBuffer.WriteLine("  return type = {0}, return value = ", context.ReturnType.FullName);
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
                    TraceBuffer.WriteLine("  return type = {0}, return value = (1)", context.ReturnType.FullName, returnValue);
                }
            }


            //Type t = context.GetProperty("target").GetType();
            ////Console.WriteLine("Tracing IN method: "+t.get_Name() + "." + rm.getSelector());
            //System.Reflection.MethodInfo[] mi = t.GetMethods(BindingFlags.Public
            //    | BindingFlags.NonPublic | BindingFlags.DeclaredOnly | BindingFlags.Instance);
            //for (int i = 0; i < mi.Length; i++)
            //{
            //    //Console.WriteLine("\tSearching for method: "+rm.getSelector()+" == "+mi[i].get_Name());
            //    if (mi[i].Name == (string)context.GetProperty("selector"))
            //    {
            //        if (((object[])context.GetProperty("args")).Length == 0)
            //        {
            //            Object[] obj = new Object[0];
            //            Console.WriteLine("TracingOUT: " + t.Name + "." + (string)context.GetProperty("selector"));
            //            break;
            //        }
            //        int k = 0;
            //        ArrayList list = new ArrayList();
            //        System.Reflection.ParameterInfo[] pi = mi[i].GetParameters();
            //        for (int j = 0; j < pi.Length; j++)
            //        {
            //            if (pi[j].IsOut)
            //            {
            //                list.Add(context.GetProperty("Arg[" + k + "]"));
            //            }
            //            k++;
            //        }
            //        Console.WriteLine("TracingOUT[" + context.GetProperty("returnvalue") + "][" + list.ToArray().Length + "]: " + list.ToString());
            //        break;
            //    }
            //}
        }
    }
}
