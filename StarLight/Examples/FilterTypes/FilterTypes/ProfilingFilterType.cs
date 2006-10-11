using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.ContextInfo.FilterTypes;

namespace FilterTypes
{
    [FilterTypeAttribute("Profiling", "StartTimerAction", "ContinueAction", 
        "StopTimerAction", "ContinueAction")]
    public class ProfilingFilterType : FilterType
    {
    }
}
