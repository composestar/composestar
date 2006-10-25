using System;
using System.Collections.Generic;
using System.Text;
using Composestar.StarLight.Filters.FilterTypes;

namespace Composestar.StarLight.Filters.BuildIn
{
    /// <summary>
    /// Performs a dispatch to another method.
    /// </summary>
    [FilterTypeAttribute(FilterType.DispatchFilter, FilterAction.DispatchAction, FilterAction.ContinueAction, FilterAction.ContinueAction,FilterAction.ContinueAction)]
    public class DispatchFilterType : FilterType
    {
    }
}
