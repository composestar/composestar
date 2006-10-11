using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo.FilterTypes;

namespace BasicTests
{
    [FilterTypeAttribute("Logging", "LoggingAction", "ContinueAction", "ContinueAction", "ContinueAction")]
    class LoggingFilterType : FilterType
    {
    }
}
