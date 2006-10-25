using System;
using System.Collections.Generic;
using System.Text;
using Composestar.StarLight.Filters.FilterTypes;

namespace Composestar.StarLight.Filters.BuildIn
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
