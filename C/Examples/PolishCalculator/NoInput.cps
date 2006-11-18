concern NoInput
{
	filtermodule noinputfm
	{
		inputfilters
			noinput : Dispatch = { [*.Getop] *.returnNumber} 
	}
	

	superimposition
	{
			
		selectors
			NoCommandInput={Class|isClassWithName(Class,'GetOperand')}; //Selector aanpassen wanneer je hem wil testen vanaf commandline (om input mee te geven).			
		filtermodules
			NoCommandInput <- noinputfm;
		
	}
}