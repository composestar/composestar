concern TraceDeviceA in DeviceController.Concerns
{
	filtermodule Trace
	{
		conditions		// declare used conditions
			doTrace : DeviceController.Configuration.DeviceConfiguration.DoTraceDeviceA();	// condition to enable/disable runtime tracing
		inputfilters	// define the inputfilters
			trace_DoSomething : Tracing = {doTrace => [*.DoSomething] };	// Trace input parameters for the DoSomething method
			trace_Result : Tracing = {doTrace => [*.get_Result] }			// Trace return value of the Result property
	}
	
	superimposition
	{
		selectors		// Selector to identity all classes belonging to DeviceA, e.g. isNamespaceWithName(NS,'DeviceController.Devices.DeviceA') , namespaceHasClass(NS, C)
			baseClass = { C | isClassWithName(C , 'DeviceController.Devices.DeviceA.DeviceA') };
		filtermodules	
			baseClass <- Trace;
	}
}    