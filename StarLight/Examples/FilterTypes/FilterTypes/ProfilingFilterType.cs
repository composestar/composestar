using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.ContextInfo.FilterTypes;
using Composestar.StarLight.ContextInfo.FilterTypes.BuildIn;
    
namespace FilterTypes
{
    [FilterTypeAttribute("Profiling", typeof(StartTimerAction), typeof(ContinueAction), 
        typeof(StopTimerAction), typeof(ContinueAction))]
    public class ProfilingFilterType : FilterType
    {
    }
}
