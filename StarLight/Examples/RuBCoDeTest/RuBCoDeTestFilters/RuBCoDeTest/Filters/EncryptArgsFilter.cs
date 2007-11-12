using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Filters.FilterTypes;

namespace RuBCoDeTestFilters.Filters
{
    [FilterTypeAttribute("EncryptArgs", "EncryptArgsAction", FilterAction.ContinueAction, "DecryptArgsAction", FilterAction.ContinueAction)]
    public class EncryptArgsFilter : FilterType
    {
    }
}
