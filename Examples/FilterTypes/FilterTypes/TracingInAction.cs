using System;
using System.Collections.Generic;
using System.Text;
using System.Reflection;
using System.Collections;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.ContextInfo.FilterTypes;

namespace FilterTypes
{
    [FilterActionAttribute("TracingInAction", FilterFlowBehaviour.Continue, 
        MessageSubstitutionBehaviour.Original)]
    public class TracingInAction : FilterAction
    {
        public override void Execute(JoinPointContext context)
        {
            if (context == null)
            {
                TraceFile.WriteLine("IN Tracing: Context not set!");
                return;
            }

            String sender = "unknown";
            if (context.Sender != null) sender = context.Sender.GetType().FullName;

            String target = "unknown";
            if (context.StartTarget != null) target = context.StartTarget.GetType().FullName;

            TraceFile.WriteLine("IN Tracing: Sender={0}, Target={1}, Selector={2} ", sender, target, context.StartSelector);

            foreach (short argOrdinal in context.GetArguments.Keys)
            {
                ArgumentInfo arg = context.GetArguments[argOrdinal];

                if (arg.Value == null)
                {
                    TraceFile.WriteLine("  argument {0} = null", argOrdinal);
                    continue;
                }

                if (arg.Type.IsPrimitive || arg.Type.FullName == "System.String")
                {
                    String argvalue = arg.Value.ToString();
                    TraceFile.WriteLine("  argument {0} -> {1} = {2}", argOrdinal, arg.Type.FullName, argvalue);
                }
                else
                {
                    TraceFile.WriteLine("  argument {0} -> {1}", argOrdinal, arg.Type.FullName);
                }
               
                //(value & flag) == flag
                if ((arg.Attributes & ArgumentAttributes.In) == ArgumentAttributes.In)
                    TraceFile.WriteLine("  input");
                if ((arg.Attributes & ArgumentAttributes.Out) == ArgumentAttributes.Out)
                    TraceFile.WriteLine("  output");
                if ((arg.Attributes & ArgumentAttributes.Optional) == ArgumentAttributes.Optional)
                    TraceFile.WriteLine("  optional");
                
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
