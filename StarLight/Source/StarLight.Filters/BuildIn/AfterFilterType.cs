using System;
using System.Collections.Generic;
using System.Text;
using Composestar.StarLight.Filters.FilterTypes;

namespace Composestar.StarLight.Filters.BuildIn
{
    /// <summary>
    /// A call to a method, implemented by the <see cref="T:AdviceAction"></see>. 
    /// </summary>
    [FilterTypeAttribute(FilterType.AfterFilter, typeof(ContinueAction), typeof(ContinueAction), typeof(AdviceAction), typeof(ContinueAction))]
    public class AfterFilterType : FilterType
    {
    }
}
