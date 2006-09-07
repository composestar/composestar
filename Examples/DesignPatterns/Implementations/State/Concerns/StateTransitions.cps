concern StateTransitions
{
	filtermodule transitions
	{
		//externals because transitions and behavior depend on common state
		externals
			transitions : Composestar.Patterns.State.StateTransition = Composestar.Patterns.State.StateTransition.instance();

		conditions
			isEmpty : Composestar.Patterns.State.Queue.isEmpty();
			isNormal : Composestar.Patterns.State.Queue.isNormal();
				
		//Meta filters because state change can occur before or after a forward
		inputfilters
			empty_Insert_Normal : Meta	= {isEmpty => [*.insert] transitions.empty_Insert_Normal}; 
			normal_Insert_Full	: Meta	= {isNormal => [*.insert] transitions.normal_Insert_Full};
			normal_Remove_Empty	: Meta	= {isNormal => [*.removeFirst] transitions.normal_Remove_Empty};
			full_Remove_Normal	: Meta	= { [*.removeFirst] transitions.full_Remove_Normal} 
	}
}	