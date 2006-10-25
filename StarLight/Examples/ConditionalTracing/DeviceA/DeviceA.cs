using System;
using System.Collections.Generic;
using System.Text;

namespace DeviceController.Devices.DeviceA
{
    /// <summary>
    /// Device A
    /// </summary>
    public class DeviceA
    {
        private string _id = "DeviceA";
        private string _result = "";

        /// <summary>
        /// The ID of this device.
        /// </summary>
        /// <returns>The ID.</returns>
        public string GetID()
        {
            return _id;
        }

        /// <summary>
        /// Execute commands on this device, tracing for this method is enabled by concern Trace.
        /// </summary>
        /// <param name="command">The command for this device to execute.</param>
        public void DoSomething(string command)
        {
            _result = String.Format("{0} executed command '{1}'.", GetID(), command);
        }
 
        /// <summary>
        /// Gets the result from the device after a call to DoSomething, tracing for this method is enabled by concern Trace.
        /// </summary>
         public String Result
        {
            get { return _result;  }
        }
    }
}
