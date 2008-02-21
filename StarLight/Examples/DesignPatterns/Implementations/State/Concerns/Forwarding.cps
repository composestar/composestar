concern StateForwarding
{
	filtermodule forwarding
	{
		//externals because transitions and behavior depend on common state
		externals
			empty : Composestar.Patterns.State.QueueEmpty = Composestar.Patterns.State.QueueEmpty.instance();
			normal : Composestar.Patterns.State.QueueNormal = Composestar.Patterns.State.QueueNormal.instance();
			full : Composestar.Patterns.State.QueueFull = Composestar.Patterns.State.QueueFull.instance();

		conditions
			isEmpty : Composestar.Patterns.State.Queue.isEmpty();
			isNormal : Composestar.Patterns.State.Queue.isNormal();
		
		inputfilters
			itIsEmpty : Dispatch = {isEmpty => <empty.*> empty.*};
			itIsNormal : Dispatch = {isNormal => <normal.*> normal.*};
			itIsFull : Dispatch	= { <full.*> full.* }
	}
}	