concern TraceDeviceA in DeviceController.Concerns
{
	filtermodule Trace
	{
		conditions		// declare used conditions
			doTrace : DeviceController.Configuration.DeviceConfiguration.DoTrace();
		inputfilters	// define the inputfilters
			trace_DoSomething : Tracing = {doTrace => [*.DoSomething] };	// Trace input parameters
			trace_Result : Tracing = {doTrace => [*.get_Result] }			// Trace return value
	}
	
	superimposition
	{
		selectors  // TODO: introduce a nice selector 
			deviceAClasses = { C | isClassWithName(C , 'DeviceController.Devices.DeviceA.DeviceA') };
			deviceBClasses = { C | isClassWithName(C , 'DeviceController.Devices.DeviceB.DeviceB') };
		filtermodules	
			deviceAClasses <- Trace;
			deviceBClasses <- Trace;
	}
}    