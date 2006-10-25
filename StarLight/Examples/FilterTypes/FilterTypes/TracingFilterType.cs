using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo.FilterTypes;

namespace FilterTypes
{
    [FilterTypeAttribute("Tracing", "TracingInAction", FilterAction.ContinueAction, 
        "TracingOutAction", FilterAction.ContinueAction)]
    public class TracingFilterType : FilterType
    {
    }
}
