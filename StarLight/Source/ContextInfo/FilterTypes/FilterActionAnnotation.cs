using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.FilterTypes
{
    [AttributeUsage(AttributeTargets.Method, Inherited = false, AllowMultiple = false)]
    class FilterActionAnnotation : Attribute
    {
        public string actionclass = "";

        public FilterActionAnnotation(string tmp)
        {
            this.actionclass = tmp;
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
