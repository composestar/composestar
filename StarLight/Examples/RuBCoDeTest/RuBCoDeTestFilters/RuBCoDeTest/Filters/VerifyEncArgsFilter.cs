using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Filters.FilterTypes;

namespace RuBCoDeTestFilters.Filters
{
    [FilterTypeAttribute("VerifyEncBefore", "VerifyEncArgsAction", FilterAction.ContinueAction, FilterAction.ContinueAction, FilterAction.ContinueAction)]
    public class VerifyEncBeforeFilter : FilterType
    {
    }

    [FilterTypeAttribute("VerifyEncAfter", FilterAction.ContinueAction, FilterAction.ContinueAction, "VerifyEncArgsAction", FilterAction.ContinueAction)]
    public class VerifyEncAfterFilter : FilterType
    {
    }
}
