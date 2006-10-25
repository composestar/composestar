using System;
using System.Collections.Generic;
using System.Configuration;
using System.Text;

namespace DeviceController.Configuration
{
    /// <summary>
    /// Device Configuration
    /// </summary>
    public class DeviceConfiguration
    {
        private static bool _doTraceDeviceA = false;
        private static bool _doTraceDeviceB = false;

        /// <summary>
        /// Load the configuration
        /// </summary>
        public static void Load()
        {
            if (ConfigurationManager.AppSettings.Get("TraceDeviceA").Equals("yes")) _doTraceDeviceA = true;
            if (ConfigurationManager.AppSettings.Get("TraceDeviceB").Equals("yes")) _doTraceDeviceB = true;
        }

        /// <summary>
        /// Gets the trace status for device A
        /// </summary>
        /// <returns>true if tracing is enabled for device A, false otherwise</returns>
        public static bool DoTraceDeviceA()
        {
           return _doTraceDeviceA; 
        }
        
        /// <summary>
        /// Gets the trace status for device B
        /// </summary>
        /// <returns>true if tracing is enabled for device B, false otherwise</returns>
        public static bool DoTraceDeviceB()
        {
            return _doTraceDeviceB;
        }
    }
}
