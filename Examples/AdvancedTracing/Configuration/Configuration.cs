using System;
using System.Collections.Generic;
using System.Configuration;
using System.Text;

namespace DeviceController.Configuration
{
    public class DeviceConfiguration
    {
        //private static DeviceConfiguration instance = null;
        
        private static bool _doTraceDeviceA = false;
        private static bool _doTraceDeviceB = false;

        public static void Load()
        {
            if (ConfigurationManager.AppSettings.Get("TraceDeviceA").Equals("yes")) _doTraceDeviceA = true;
            if (ConfigurationManager.AppSettings.Get("TraceDeviceB").Equals("yes")) _doTraceDeviceB = true;
        }

        public static bool DoTraceDeviceA()
        {
           return _doTraceDeviceA; 
        }
        
        public static bool DoTraceDeviceB()
        {
            return _doTraceDeviceB;
        }
    }
}
