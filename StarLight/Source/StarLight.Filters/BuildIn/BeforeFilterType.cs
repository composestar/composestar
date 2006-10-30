using System;
using System.Collections.Generic;
using System.Text;
using Composestar.StarLight.Filters.FilterTypes;

namespace Composestar.StarLight.Filters.BuildIn
{
    /// <summary>
    /// The before filter implements a call to a method.
    /// </summary>
    [FilterTypeAttribute(FilterType.BeforeFilter, FilterAction.AdviceAction, FilterAction.ContinueAction,FilterAction.ContinueAction,FilterAction.ContinueAction)]
    public class BeforeFilterType : FilterType
    {
    }
}
