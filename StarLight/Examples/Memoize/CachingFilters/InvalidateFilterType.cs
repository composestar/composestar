using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.Filters.FilterTypes;

namespace CachingFilters
{
    [FilterTypeAttribute("Invalidate", FilterAction.ContinueAction, FilterAction.ContinueAction, 
        "InvalidateAction", FilterAction.ContinueAction)]
    public class InvalidateFilterType : FilterType
    {
    }
}
