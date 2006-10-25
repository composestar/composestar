using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.Filters.FilterTypes;

namespace FilterTypes
{
    [FilterTypeAttribute("Tracing", "TracingInAction", FilterAction.ContinueAction, 
        "TracingOutAction", FilterAction.ContinueAction)]
    public class TracingFilterType : FilterType
    {
    }
}
