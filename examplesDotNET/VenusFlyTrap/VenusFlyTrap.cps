concern VenusFlyTrap in VenusFlyTrapExample {
	filtermodule InsectTrap{
		internals
			a : VenusFlyTrapExample.Animal;
		conditions
			isfly : a.hasPrey();
		inputfilters
			eat : Dispatch = {[*.catchFly] a.catchPrey };
			grow : Dispatch = {isfly => [*.grow] a.grow }
	}
	
	superimposition{
		selectors
			files = { C | isClassWithName(C,'VenusFlyTrapExample.VenusFlyTrap') };
		filtermodules
			files <-InsectTrap;
	}
	
	implementation by VenusFlyTrapExample.VenusFlyTrap.dll;
}