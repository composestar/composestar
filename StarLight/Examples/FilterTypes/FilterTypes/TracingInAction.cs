using System;
using System.Collections.Generic;
using System.Text;
using System.Reflection;
using System.Collections;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Filters.FilterTypes;

namespace FilterTypes
{
    [FilterActionAttribute("TracingInAction", FilterActionAttribute.FilterFlowBehaviour.Continue,
       FilterActionAttribute.MessageSubstitutionBehaviour.Original)]
    public class TracingInAction : FilterAction
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
                for (short i=1; i <= context.ArgumentCount; i++)
                {
                    ArgumentInfo argumentInfo = context.GetArgumentInfo(i);
                    
                    string argdirection = "-";
                    if ((argumentInfo.Attributes & ArgumentAttributes.In) == ArgumentAttributes.In)
                        argdirection = "input";
                    if ((argumentInfo.Attributes & ArgumentAttributes.Out) == ArgumentAttributes.Out)
                        argdirection = "output";
                    if ((argumentInfo.Attributes & ArgumentAttributes.Optional) == ArgumentAttributes.Optional)
                        argdirection = "optional";
                    
                    if (argumentInfo.Value == null)
                    {
                        TraceBuffer.WriteLine("  argument {0} ({2}) -> {1} = null", i, context.GetArgumentType(i), argdirection);
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
                    TraceBuffer.WriteLine("  argument {0} ({3}) -> {1} = {2}", i, context.GetArgumentType(i).FullName, argvalue, argdirection);
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
            //            Console.WriteLine("TracingIN: " + t.Name + "." + (string)context.GetProperty("selector"));
            //            break;
            //        }
            //        int k = 0;
            //        ArrayList list = new ArrayList();
            //        System.Reflection.ParameterInfo[] pi = mi[i].GetParameters();
            //        for (int j = 0; j < pi.Length; j++)
            //        {
            //            if (!(pi[j].IsOut || !pi[j].IsRetval))
            //            {
            //                list.Add(context.GetProperty("Arg[" + k + "]"));
            //            }
            //            k++;
            //        }
            //        Console.WriteLine("TracingIN[" + list.ToArray().Length + "]: " + list.ToString());
            //        break;
            //    }
            //}
        }
    }
}
