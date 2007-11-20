using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Filters.FilterTypes;
using Composestar.StarLight.Filters.BuiltIn;
using Composestar.StarLight.ContextInfo.RuBCoDe;

[assembly: ConflictRule("frequency", "(write)(write)+(read)", true)]
[assembly: ConflictRule("starttime", "(write)(write)+(read)", true)]
[assembly: Resource("frequency", "read,write")]
[assembly: Resource("starttime", "read,write")]
namespace CustomFilters
{
    [FilterTypeAttribute("Profiling", typeof(StartTimerAction), typeof(ContinueAction), 
        typeof(StopTimerAction), typeof(ContinueAction))]
    public class ProfilingFilterType : FilterType
    {
    }
}
