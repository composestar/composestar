using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Filters.FilterTypes;   

namespace BasicTests
{
	[FilterTypeAttribute("NotImplemented",  FilterAction.ContinueAction, "NotImplementedAction", FilterAction.ContinueAction, FilterAction.ContinueAction)]
	class NotImplementedFilterType : FilterType
	{
	}
}
