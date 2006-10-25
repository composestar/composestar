using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Filters.FilterTypes;

namespace FilterTypes
{
    [FilterTypeAttribute("Empty", "StartEmptyAction", FilterAction.ContinueAction, 
        "StopEmptyAction", FilterAction.ContinueAction)]
    public class EmptyFilterType : FilterType
    {
    }
}
