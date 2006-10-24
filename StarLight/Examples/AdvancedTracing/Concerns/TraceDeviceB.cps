concern TraceDeviceB in DeviceController.Concerns
{
	filtermodule Trace
	{
		//externals		// declare used external (global) objects
		//	config : DeviceController.Configuration.DeviceConfiguration = DeviceController.Configuration.DeviceConfiguration.GetInstance();
		conditions		// declare used conditions
			doTrace : DeviceController.Configuration.DeviceConfiguration.DoTraceDeviceB();
		inputfilters	// define the inputfilters
			trace_DoSomething : Tracing = {doTrace => [*.DoSomething] };	// Trace input parameters
			trace_Result : Tracing = {doTrace => [*.get_Result] }			// Trace return value
	}
	
	superimposition
	{
		selectors		// Selector to identity all classes belonging to DeviceA, e.g. isNamespaceWithName(NS,'DeviceController.Devices.DeviceA') , namespaceHasClass(NS, C) 
			baseClass = { C | isClassWithName(C , 'DeviceController.Devices.DeviceB.DeviceB') };
		filtermodules
			baseClass <- Trace;
	}
}    