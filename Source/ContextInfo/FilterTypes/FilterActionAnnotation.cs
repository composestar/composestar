using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.FilterTypes
{
    [AttributeUsage(AttributeTargets.Class, Inherited = false, AllowMultiple = false)]
    public class FilterActionAnnotation : Attribute
    {
        public string actionName;
        public FilterFlowBehaviour flowBehaviour;
        public MessageSubstitutionBehaviour substitutionBehaviour;


        /// <summary>
        /// Enumeration to indicate how a certain FilterAction influences the flow through the 
        /// filterset.
        /// <para>There are three options possible:
        /// <list type="Bullet">
        /// <item><description>Continue: To indicate that flow continues to the next filter</description></item>
        /// <item><description>Exit: To indicate that flow exits the filterset without a return,
        /// for example with an Error action</description></item>
        /// <item><description>Return: To indicate that flow changes from call to return, 
        /// for example with a Dispatch action</description></item>
        /// </list>
        /// </para>
        /// </summary>
        public enum FilterFlowBehaviour
        {
            Continue, Exit, Return
        }

        /// <summary>
        /// Enumeration to indicate how the action changes the message
        /// <para>There are three options possible:
        /// <list type="Bullet">
        /// <item><description>Original: The message is not changed</description></item>
        /// <item><description>Substituted: The message is changed according to the substitutionpart</description></item>
        /// <item><description>Any: The message can change into any other message. 
        /// Be carefull when using this option, as it introduces more uncertainty in the static reasoning algorithms. 
        /// Use only this option when you cannot use a following substitution filter</description></item>
        /// </list>
        /// </para>
        /// </summary>
        public enum MessageSubstitutionBehaviour
        {
            Original, Substituted, Any
        }


        public FilterActionAnnotation(string name, FilterFlowBehaviour flowBehaviour, 
            MessageSubstitutionBehaviour substitutionBehaviour)
        {
            this.actionName = name;
            this.flowBehaviour = flowBehaviour;
            this.substitutionBehaviour = substitutionBehaviour;
        }
    }

    [AttributeUsage(AttributeTargets.Method, Inherited = false, AllowMultiple = false)]
    class FilterActionSpecificationAnnotation : Attribute
    {
        public string spec;

        public FilterActionSpecificationAnnotation(string spec)
        {
            this.spec = spec;
        }

        private void parseSpecification()
        {
            // Input: target.write(foo)& selector.write(foo)
            string[] rawops = spec.Split("&".ToCharArray());
            for (int i = 0; i <= rawops.Length; i++)
            {
                Resource rsrc = null;
                string[] parts = rawops[i].Split(".".ToCharArray()); // [target][write(foo,bar)]
                if (parts.Length == 2)
                {
                    rsrc = new Resource();
                    rsrc.Name = parts[0];
                    string[] opparts = parts[1].Split("(".ToCharArray()); // [write][foo,bar)]
                    if (opparts.Length == 2)
                    {
                        Operation op = new Operation();
                        op.Name = parts[0];
                        string[] argparts = opparts[1].Split(",".ToCharArray()); // [foo][bar)]
                        for (int j = 0; j < argparts.Length; j++)
                        {
                            string argname = argparts[j];
                            if (argname.EndsWith(")"))
                                argname = argname.Substring(0, argname.Length - 1);
                            Argument arg = new Argument();
                            arg.Name = argname;
                            op.args.Add(arg);
                        }
                        rsrc.operations.Add(op);
                    }
                }
            }
        }




    }
}
