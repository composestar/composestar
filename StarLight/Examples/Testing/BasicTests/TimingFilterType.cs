using System;
using System.Collections.Generic;
using System.Text;
using Composestar.StarLight.Filters.FilterTypes;

namespace BasicTests
{
	[FilterTypeAttribute("Timing", "TimingStartAction", FilterAction.ContinueAction,
	  "TimingStopAction", FilterAction.ContinueAction)]
	public class TimingFilterType : FilterType
	{
	}
}
