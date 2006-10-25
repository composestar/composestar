using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.FilterTypes.BuildIn
{
    /// <summary>
    /// Performs a dispatch to another method.
    /// </summary>
    [FilterTypeAttribute(FilterType.DispatchFilter, FilterAction.DispatchAction, FilterAction.ContinueAction, FilterAction.ContinueAction,FilterAction.ContinueAction)]
    public class DispatchFilterType : FilterType
    {
    }
}
