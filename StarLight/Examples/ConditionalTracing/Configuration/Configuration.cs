using System;
using System.Collections.Generic;
using System.Configuration;
using System.Text;

namespace DeviceController.Configuration
{
    public class DeviceConfiguration
    {
        private static bool _doTraceDeviceA = false;
        private static bool _doTraceDeviceB = false;

        public static void Load()
        {
            if (ConfigurationManager.AppSettings.Get("TraceDeviceA").Equals("yes")) _doTraceDeviceA = true;
            if (ConfigurationManager.AppSettings.Get("TraceDeviceB").Equals("yes")) _doTraceDeviceB = true;
        }

        public static bool DoTrace(Composestar.StarLight.ContextInfo.JoinPointContext context)
        {
            // TODO: base return value on the context info
            if (_doTraceDeviceA && _doTraceDeviceB) return true;

            return false;
        }
        
    }
}
