using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.CoreServices.ILWeaver
{
    /// <summary>
    /// Contains weave statistics
    /// </summary>
    public class WeaveStatistics
    {
        public uint InternalsAdded = 0;
        public uint ExternalsAdded = 0;
        public uint OutputFiltersAdded = 0;
        public uint InputFiltersAdded = 0;

        public uint TypesProcessed = 0;
        public uint MethodsProcessed = 0;

        public TimeSpan TotalWeaveTime = TimeSpan.Zero;
        public TimeSpan TotalTypeWeaveTime = TimeSpan.Zero;
        public TimeSpan TotalMethodWeaveTime = TimeSpan.Zero;

        /// <summary>
        /// Average weave time per type
        /// </summary>
        /// <returns>Time span</returns>
        public TimeSpan AverageWeaveTimePerType
        {
            get
            {
                return TimeSpan.FromTicks(TotalTypeWeaveTime.Ticks / TypesProcessed);
            } // get
        } // AverageWeaveTimePerType

        /// <summary>
        /// Average weave time per method
        /// </summary>
        /// <returns>Time span</returns>
        public TimeSpan AverageWeaveTimePerMethod
        {
            get
            {
                return TimeSpan.FromTicks(TotalMethodWeaveTime.Ticks / MethodsProcessed);
            } // get
        } // AverageWeaveTimePerMethod

        public TimeSpan MaxWeaveTimePerType = TimeSpan.Zero;
        public TimeSpan MaxWeaveTimePerMethod = TimeSpan.Zero;
    }
}
