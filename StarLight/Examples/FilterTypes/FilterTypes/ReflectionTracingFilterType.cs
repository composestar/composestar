using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.Filters.FilterTypes;

namespace FilterTypes
{
    [FilterTypeAttribute("ReflectionTracing", "ReflectionTracingInAction", FilterAction.ContinueAction, 
        "ReflectionTracingOutAction", FilterAction.ContinueAction)]
    public class ReflectionTracingFilterType : FilterType
    {
    }
}
