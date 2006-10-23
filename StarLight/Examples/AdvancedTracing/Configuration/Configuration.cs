using System;
using System.Collections.Generic;
using System.Configuration;
using System.Text;

namespace DeviceController.Configuration
{
    public class DeviceConfiguration
    {
        private static DeviceConfiguration instance = null;
        
        private bool _doTraceDeviceA = false;
        private bool _doTraceDeviceB = false;

        private DeviceConfiguration()
        {
            if (ConfigurationManager.AppSettings.Get("TraceDeviceA").Equals("yes")) _doTraceDeviceA = true;
            if (ConfigurationManager.AppSettings.Get("TraceDeviceB").Equals("yes")) _doTraceDeviceB = true;
        }

        public static DeviceConfiguration GetInstance()
        {
            if (instance == null) instance = new DeviceConfiguration();
            return instance;
        }

        public bool DoTraceDeviceA()
        {
           return _doTraceDeviceA; 
        }
        
        public bool DoTraceDeviceB()
        {
            return _doTraceDeviceB;
        }
    }
}
