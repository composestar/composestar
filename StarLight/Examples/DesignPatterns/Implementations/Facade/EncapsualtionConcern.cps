concern EncapsulationConcern
{
	filtermodule facadeEncapsulation
	{
		internals
			encapsulationConcern: Composestar.Patterns.Facade.EncapsulationConcern;	

		inputfilters
			encapsulation : Meta = { [*.*] encapsulationConcern.giveWarning }
	}
	superimposition
	{
		selectors
			subsystems = { Class | classHasAnnotationWithName(Class, 'Composestar.Patterns.Facade.Annotations.SubsystemClass' ) };
				
		filtermodules
			subsystems <- facadeEncapsulation;
	}
	implementation in JSharp by	Composestar.Patterns.Facade.EncapsulationConcern as "EncapsulationConcern.jsl"
	{
		package Composestar.Patterns.Facade;

		import Composestar.StarLight.ContextInfo.JoinPointContext;

		public class EncapsulationConcern
		{
			public void giveWarning(JoinPointContext jpc)
			{
				Object sender = jpc.getSender();
				String className = sender.getClass().getName();
				boolean allowed = className.endsWith("OutputFacade");
				if(!allowed){
					System.out.println("Warning: Trying to circumvent encapsulation of Facade");
					message.reply();
				}
			}
		}
	}
}
