using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Filters.FilterTypes;   

namespace BasicTests
{
	[FilterTypeAttribute("Logging", "LoggingAction", FilterAction.ContinueAction, FilterAction.ContinueAction,FilterAction.ContinueAction)]
	class LoggingFilterType : FilterType
	{
	}
}
