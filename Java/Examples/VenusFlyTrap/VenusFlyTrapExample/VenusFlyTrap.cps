concern VenusFlyTrap in VenusFlyTrapExample
{
	filtermodule InsectTrap
	{
		internals
			a : VenusFlyTrapExample.Animal;
		conditions
			hasPrey : a.hasPrey();
		inputfilters
			eat  : Dispatch = {[*.catchFly] a.catchPrey };
			grow : Dispatch = {hasPrey => [*.grow] a.grow }
	}
	
	superimposition
	{
		selectors
			vft = { C | isClassWithName(C,'VenusFlyTrapExample.VenusFlyTrap') };
		filtermodules
			vft <- InsectTrap;
	}
	
	implementation by VenusFlyTrapExample.VenusFlyTrap;
}