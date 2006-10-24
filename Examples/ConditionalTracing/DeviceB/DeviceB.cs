using System;
using System.Collections.Generic;
using System.Text;

namespace DeviceController.Devices.DeviceB
{
    public class DeviceB
    {
        private string _id = "DeviceB";
        private string _result = "";

        public string GetID()
        {
            return _id;
        }

        public void DoSomething(string command)
        {
            _result = String.Format("{0} executed command '{1}'.", GetID(), command);
        }

        public String Result
        {
            get { return _result; }
        }
    }
}
