using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Filters.FilterTypes;

namespace CachingFilters
{
    [FilterActionAttribute("CacheReturnAction", FilterActionAttribute.FilterFlowBehavior.Continue,
       FilterActionAttribute.MessageSubstitutionBehavior.Original)]
    [ResourceOperation("target.read;selector.read;arg.read;cache.write")]
    public class CacheReturnAction : FilterAction
    {
        public override void Execute(JoinPointContext context)
        {
            storeCachedValue(context);
        }

        public static void storeCachedValue(JoinPointContext context)
        {
            if (!context.HasReturnValue)
            {
                return;
            }
            Cache cache = Cache.instance(context.CurrentTarget);
            Object args = null;
            if (context.ArgumentCount > 0)
            {
                args = context.GetArgumentValue(0);
            }
            cache.setCache(context.MethodInformation, args, context.ReturnValue);
        }
    }
}
