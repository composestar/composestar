using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.FilterTypes.BuildIn
{
    /// <summary>
    /// An error filter.   
    /// </summary>
    [FilterTypeAttribute(FilterType.ErrorFilter, FilterAction.ContinueAction, FilterAction.ErrorAction,
      FilterAction.ContinueAction, FilterAction.ContinueAction)]
    public class ErrorFilterType : FilterType
    {
    }
}
