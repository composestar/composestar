using System;
using System.Collections.Generic;
using System.Text;
using Composestar.StarLight.Filters.FilterTypes;

namespace Composestar.StarLight.Filters.BuildIn
{
    /// <summary>
    /// A call to a method, implemented by the <see cref="T:Composestar.StarLight.Filters.BuildIn.AdviceAction"></see>. 
    /// </summary>
    [FilterTypeAttribute(FilterType.AfterFilter, FilterAction.ContinueAction, FilterAction.ContinueAction, FilterAction.AdviceAction, FilterAction.ContinueAction)]
    public class AfterFilterType : FilterType
    {
    }
}
