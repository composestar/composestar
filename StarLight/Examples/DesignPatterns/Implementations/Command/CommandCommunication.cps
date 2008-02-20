concern CommandCommunication {
	filtermodule CoupleInvokerToCommand{
		internals
			bc : Composestar.Patterns.Command.ButtonCommand;

		inputfilters
			executeCommand : Dispatch = { [*.clickedDummy] bc.executeCommand }
	}
	filtermodule CoupleCommandToReceiver{
		internals
			receiver : Composestar.Patterns.Command.PrintTarget;

		inputfilters
			executeCommand : Dispatch = { [*.executeCommand] receiver.printStandard}
	}
	superimposition{
		selectors
			invoker = { Invoker | classHasAnnotationWithName(Invoker,'Composestar.Patterns.Command.Annotations.Invoker') };
			receiver = { Receiver | classHasAnnotationWithName(Receiver,'Composestar.Patterns.Command.Annotations.Receiver') };
			
		filtermodules
			invoker <- CoupleInvokerToCommand;
			receiver <- CoupleCommandToReceiver;
	}
}