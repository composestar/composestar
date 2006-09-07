concern CoR
{
	filtermodule buttonHandler
	{
		internals
			clickHandlers : Composestar.Patterns.ChainOfResponsibility.ClickHandlers;

		conditions
			isButton : Composestar.Patterns.ChainOfResponsibility.Click.isButton();

		inputfilters
			setSuccessor: Dispatch = { [*.setSuccessor] clickHandlers.setSuccessor};
			handleClick : Dispatch = { isButton => [*.handleClick] clickHandlers.buttonClickHandler};
			giveToSuccessor : Dispatch = { [*.handleClick] clickHandlers.giveToSuccessor}
	}
	filtermodule panelHandler
	{
		internals
			clickHandlers : Composestar.Patterns.ChainOfResponsibility.ClickHandlers;

		conditions
			isPanel : Composestar.Patterns.ChainOfResponsibility.Click.isPanel();

		inputfilters
			setSuccessor: Dispatch = { [*.setSuccessor] clickHandlers.setSuccessor};
			handleClick : Dispatch = { isPanel => [*.handleClick] clickHandlers.panelClickHandler};
			giveToSuccessor : Dispatch = { [*.handleClick] clickHandlers.giveToSuccessor}
	}
	filtermodule frameHandler
	{
		internals
			clickHandlers : Composestar.Patterns.ChainOfResponsibility.ClickHandlers;

		conditions
			isFrame : Composestar.Patterns.ChainOfResponsibility.Click.isFrame();

		inputfilters
			setSuccessor: Dispatch = { [*.setSuccessor] clickHandlers.setSuccessor};
			handleClick : Dispatch = { isFrame => [*.handleClick] clickHandlers.frameClickHandler};
			giveToSuccessor : Dispatch = { [*.handleClick] clickHandlers.giveToSuccessor}
	}
	superimposition
	{
		selectors
			frame = { Class | isClassWithName(Class, 'Composestar.Patterns.ChainOfResponsibility.Frame' ) };
			panel = { Class | isClassWithName(Class, 'Composestar.Patterns.ChainOfResponsibility.Panel' ) };
			button = { Class | isClassWithName(Class, 'Composestar.Patterns.ChainOfResponsibility.Button' ) };
				
		filtermodules
			frame <- frameHandler;
			panel <- panelHandler;
			button <- buttonHandler;
	}
}	