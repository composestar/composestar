using System;
using System.Collections.Generic;
using System.Text;
using Composestar.StarLight.Filters.FilterTypes;
   
namespace Composestar.StarLight.Filters.BuildIn
{
    /// <summary>
    /// Substitution filter type.
    /// </summary>
    [FilterTypeAttribute(FilterType.SubstitutionFilter, FilterAction.SubstitutionAction, FilterAction.ContinueAction,
        FilterAction.ContinueAction, FilterAction.ContinueAction)]
    public class SubstitutionFilterType : FilterType
    {
    }
}
