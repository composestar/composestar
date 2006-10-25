using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.FilterTypes.BuildIn
{
    /// <summary>
    /// A call to a method, implemented by the <see cref="T:AdviceAction"></see>. 
    /// </summary>
    [FilterTypeAttribute(FilterType.AfterFilter, typeof(ContinueAction), typeof(ContinueAction), typeof(AdviceAction), typeof(ContinueAction))]
    public class AfterFilterType : FilterType
    {
    }
}
