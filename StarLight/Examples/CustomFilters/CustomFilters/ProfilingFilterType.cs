using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Filters.FilterTypes;
using Composestar.StarLight.Filters.BuiltIn;

namespace CustomFilters
{
    [FilterTypeAttribute("Profiling", typeof(StartTimerAction), typeof(ContinueAction), 
        typeof(StopTimerAction), typeof(ContinueAction))]
    public class ProfilingFilterType : FilterType
    {
    }
}
