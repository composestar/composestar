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

        private static Dictionary<string, string> _deviceDictionary = null;

        /// <summary>
        /// Load the device configuration
        /// </summary>
        public static void Load()
        {
            // Mapping from class names to devices
            _deviceDictionary = new Dictionary<string, string>();
            _deviceDictionary.Add("DeviceController.Devices.DeviceA.DeviceA", "DeviceA");
            _deviceDictionary.Add("DeviceController.Devices.DeviceB.DeviceB", "DeviceB");

            // Get the individual device settings from the xml configuration file
            if (ConfigurationManager.AppSettings.Get("TraceDeviceA").Equals("yes")) _doTraceDeviceA = true;
            if (ConfigurationManager.AppSettings.Get("TraceDeviceB").Equals("yes")) _doTraceDeviceB = true;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="context"></param>
        /// <returns>True if tracing is enabled for the device the method belongs to, false otherwise.</returns>
        public static bool DoTrace(Composestar.StarLight.ContextInfo.JoinPointContext context)
        {            
            // Unable to determine the target of the call, no tracing
            if (context.StartTarget == null) return false;

            // Get the fullname of the target of the call
            string target = "";
            target = context.StartTarget.GetType().FullName;
        
            // Check if the target belongs to any device
            if (_deviceDictionary.ContainsKey(target))
            {
                // Get the device name this target belongs to
                string devicename = _deviceDictionary[target];
                
                // Return the trace setting for the device this target belongs to
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
