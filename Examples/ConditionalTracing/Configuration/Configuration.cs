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

        private static Dictionary<string, string> _deviceDictionary = null;

        public static void Load()
        {
            // Mapping from class names to devices
            _deviceDictionary = new Dictionary<string, string>();
            _deviceDictionary.Add("DeviceController.Devices.DeviceA.DeviceA", "DeviceA");
            _deviceDictionary.Add("DeviceController.Devices.DeviceB.DeviceB", "DeviceB");

            if (ConfigurationManager.AppSettings.Get("TraceDeviceA").Equals("yes")) _doTraceDeviceA = true;
            if (ConfigurationManager.AppSettings.Get("TraceDeviceB").Equals("yes")) _doTraceDeviceB = true;
        }

        public static bool DoTrace(Composestar.StarLight.ContextInfo.JoinPointContext context)
        {            
            if (context.StartTarget == null) return false;

            string target = "";
            target = context.StartTarget.GetType().FullName;
        
            if (_deviceDictionary.ContainsKey(target))
            {
                string devicename = _deviceDictionary[target];
                if ( devicename == "DeviceA" ) 
                {
                    return _doTraceDeviceA;
                }
                else if ( devicename == "DeviceB" )
                {
                    return _doTraceDeviceB;
                }
            }

            return false;
        }
        
    }
}
