using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.Filters.FilterTypes;
using Composestar.StarLight.ContextInfo.RuBCoDe;

[assembly: ConflictRule("cache", "(write)(![write,read]*(write)![write,read]*)+(read)", true, "previous cached value overwritten")]
[assembly: Resource("cache", "read,write")]

namespace CachingFilters
{
    [FilterTypeAttribute("Cache", "CacheAction", FilterAction.ContinueAction,
        "CacheReturnAction", FilterAction.ContinueAction)]
    public class CacheFilterType : FilterType
    {
    }
}
