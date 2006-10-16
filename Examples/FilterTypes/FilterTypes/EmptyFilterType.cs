using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.ContextInfo.FilterTypes;

namespace FilterTypes
{
    [FilterTypeAttribute("Empty", "StartEmptyAction", "ContinueAction", 
        "StopEmptyrAction", "ContinueAction")]
    public class EmptyFilterType : FilterType
    {
    }
}
