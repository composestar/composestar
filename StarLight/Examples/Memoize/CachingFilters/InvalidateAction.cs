using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Filters.FilterTypes;

namespace CachingFilters
{
    [FilterActionAttribute("InvalidateAction", FilterActionAttribute.FilterFlowBehavior.Continue,
       FilterActionAttribute.MessageSubstitutionBehavior.Original)]
    [ResourceOperation("target.read;selector.read;arg.read;cache.write")]
    public class InvalidateAction : FilterAction
    {
        public override void Execute(JoinPointContext context)
        {
            clearCache(context);
        }

        public static void clearCache(JoinPointContext context)
        {
            if (context.ReturnType == null || context.ReturnType == typeof(void))
            {
                return;
            }
            Cache cache = Cache.instance(context.CurrentTarget);
            cache.clearCache();
        }
    }
}
