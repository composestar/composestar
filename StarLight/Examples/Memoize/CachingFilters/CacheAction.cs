using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Filters.FilterTypes;

namespace CachingFilters
{
    [FilterActionAttribute("CacheAction", FilterActionAttribute.FilterFlowBehavior.Continue,
       FilterActionAttribute.MessageSubstitutionBehavior.Original)]
    [ResourceOperation("target.read;selector.read;arg.read;cache.read;return.write")]
    public class CacheAction : FilterAction
    {
        public override void Execute(JoinPointContext context)
        {
            getCachedValue(context);
        }

        public static bool getCachedValue(JoinPointContext context)
        {
            if (context.ReturnType == null || context.ReturnType == typeof(void))
            {
                return false;
            }
            Cache cache = Cache.instance(context.CurrentTarget);
            Object args = null;
            if (context.ArgumentCount > 0)
            {
                args = context.GetArgumentValue(0);
            }
            Object val;
            if (cache.getCache(context.MethodInformation, args, out val))
            {
                context.ReturnValue = val;
                return true;
            }
            return false;
        }
    }
}
