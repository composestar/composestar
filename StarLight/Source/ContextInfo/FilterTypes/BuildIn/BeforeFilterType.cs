using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.FilterTypes.BuildIn
{
    /// <summary>
    /// The before filter implements a call the a method.
    /// </summary>
    [FilterTypeAttribute(FilterType.BeforeFilter, FilterAction.AdviceAction, FilterAction.ContinueAction,FilterAction.ContinueAction,FilterAction.ContinueAction)]
    public class BeforeFilterType : FilterType
    {
    }
}
