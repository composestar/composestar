using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.FilterTypes.BuildIn
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
