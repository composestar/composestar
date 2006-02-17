	VENUS FLYTRAP

	The venus' flytrap is probably the most famous of North America's carnivorous plants. 
	Despite its familiarity, until recently its range is restricted to the Cape Fear area of North Carolina. 
	This graceful plant is equipped with paired leaves which work on the principle of a snap-jaw trap. 
	When a fly lands on specialized trigger hairs between these leaves, the leaves will snap together, 
	imprisoning the fly. These leaves will then remain closed until the fly has been digested. 
	
Now, assume as a scientist, you make this claim:

"The plant, in fact, can live without eating insects. Like normal plants, it does photosynthesis using sunlight, 
water and other ingredients from soil. But insects are rich resources for nitrogen and this plant utilizes this 
resource for better survival."

And you want to prove your claim, by controlled experimentation using more than one plants. You set up the experiment
environment such that provide either feed the plant with flies, or give sunshine and water in order to let it do 
photosynthesis. 
	
In this example, we try to address the irregularity (as opposed to being a plant) of venus flytrap using composition filters approach. 
Assume you modelled simple inheritance hierarchy to group living beings into two different subclasses and enhanced reuse of 
functionality and data as follows:

Abstract root class LivingBeing has one method: 
	
	public abstract void grow();

Animal class inherits from LivingBeing class and provides animal specific digesting and growing functionality.

Plant class inherits from LivingBeing class and provides plant specific photosynthesis and growing functionality.

Now you come to the modeling of venus flytap in controlled experiment environment. It is naturaly a plant but
digesting functionality is an aspect which is similar to that of an animal. We can create a new concern for solving 
this problem and use a simple dispatch filter to provide dynamic dispatch depending on the environmental conditions:

// Concern definition for VenusFlyTrap
concern VenusFlyTrap{

	//Filter module which provides dynamic dispatch functionality
	filtermodule FlyTrap{
	
		//VenusFlyTrap concern has one internal field
		internals
		
			//Animal object is going to be used to provide digesting dunctionality.
			a : Animal;
			
		//This condition is dynamically set during runtime
		conditions
		
			isFlyTrapped : inner.isFlyTrapped();
			
		//These filters intercept incoming messages to the object
		inputfilters
		
			//Whenever a grow message is sent, it is dispatched to the animal if there a fly is trapped.
			disp : Dispatch = {isFlyTrapped => [*.grow] a.grow }
	}
	
	//This part determines on which objects, what filters are going to be superimposed
	superimposition{
		
		//Select the set of objects on which the 
		//filters are going to be superimposed
		selectors
		
			//Select all instances of VenusFlyTrap class
			files = { *=VenusFlyTrapExample.VenusFlyTrap };
		
		//Superimpose the filtermodules onto the selected objects	
		filtermodules
		
			//Superimpose multiple inheritance filter module 
			//onto all instances of VenusFlyTrap class
			files <-FlyTrap;
	}
}

	
							
							
							
							