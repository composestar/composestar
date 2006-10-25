using System;
using System.Collections.Generic;
using System.Text;

namespace DeviceController.Devices.DeviceB
{
    /// <summary>
    /// Device B
    /// </summary>
    public class DeviceB
    {
        private string _id = "DeviceB";
        private string _result = "";

        /// <summary>
        /// Gets the ID for this Device.
        /// </summary>
        /// <returns>The ID.</returns>
        public string GetID()
        {
            return _id;
        }

        /// <summary>
        /// Execute commands on this device, tracing for this method is enabled by concern TraceDeviceB.
        /// </summary>
        /// <param name="command">The command for this device to execute.</param>
        public void DoSomething(string command)
        {
            _result = String.Format("{0} executed command '{1}'.", GetID(), command);
        }

        /// <summary>
        /// Gets the result from the device after a call to DoSomething, tracing for this method is enabled by concern TraceDeviceB.
        /// </summary>
        public String Result
        {
            get { return _result; }
        }
    }
}
