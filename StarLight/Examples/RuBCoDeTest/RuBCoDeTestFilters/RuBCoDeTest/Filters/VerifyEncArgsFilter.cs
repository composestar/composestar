using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Filters.FilterTypes;

namespace RuBCoDeTestFilters.Filters
{
    [FilterTypeAttribute("VerifyEncArgs", "VerifyEncArgsAction", FilterAction.ContinueAction, FilterAction.ContinueAction, FilterAction.ContinueAction)]
    public class VerifyEncArgsFilter : FilterType
    {
    }
}
