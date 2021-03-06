using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Filters.FilterTypes;
using Composestar.StarLight.Filters.BuiltIn;
using Composestar.StarLight.ContextInfo.RuBCoDe;

[assembly: ConflictRule("frequency", "(write)(![write,read]*(write)![write,read]*)+(read)", true, "Resulting value is invalid")]
[assembly: ConflictRule("starttime", "(write)(![write,read]*(write)![write,read]*)+(read)", true, "Resulting value is invalid")]
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
