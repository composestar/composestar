using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Filters.FilterTypes;

namespace RuBCoDeTestFilters.Filters
{
    [FilterTypeAttribute("VerifyBefore", "VerifyArgsAction", FilterAction.ContinueAction, FilterAction.ContinueAction, FilterAction.ContinueAction)]
    public class VerifyBeforeFilter : FilterType
    {
    }

    [FilterTypeAttribute("VerifyAfter", FilterAction.ContinueAction, FilterAction.ContinueAction, "VerifyArgsAction", FilterAction.ContinueAction)]
    public class VerifyAfterFilter : FilterType
    {
    }
}
