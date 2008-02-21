concern MementoAccess{
	filtermodule mementoAccess{
		internals
			mementoAccess : Composestar.Patterns.Memento.MementoAccess;

		inputfilters
			set			: Meta		= { [*.setState] mementoAccess.setOriginator }; 
			get			: Meta		= { [*.getState] mementoAccess.checkOriginator }
	}
	superimposition{
		selectors
			memento		= { Class | classHasAnnotationWithName(Class, 'Composestar.Patterns.Memento.Annotations.Memento') };
				
		filtermodules
			memento		<- mementoAccess;
	}
	implementation in JSharp by	Composestar.Patterns.Memento.MementoAccess as	"MementoAccess.jsl"
	{
		package Composestar.Patterns.Memento;

		import Composestar.StarLight.ContextInfo.JoinPointContext;

		public class MementoAccess
		{
			public MementoAccess(){}

			private Object originator;

			//the object that sets the state is the only one that is allowed to get the state
			public void setOriginator(JoinPointContext jpc)
			{
				//memento is not set or memento is set by the originator
				if(!(originator == null) && !(originator == jpc.get_CurrentTarget()))
				{
					System.out.println("WARNING: State is not allowed to be set by another object then originator");
					// FIXME: message.reply();
				}
				else
				{
					originator = jpc.get_CurrentTarget();
					System.out.println("Setting Originator: "+originator.getClass());
				}
			}

			public void checkOriginator(JoinPointContext jpc)
			{
				System.out.println("The sender should be the originator");
				System.out.println("originator "+originator.getClass());
				System.out.println("sender "+jpc.get_CurrentTarget().getClass());
				if(!(originator == jpc.get_CurrentTarget()))
				{
					System.out.println("WARNING: Sender is not Originator and is not allowed to access state");
					//normally throw exception
					//throw new Exception("Sender is not Originator and cannot access state");
				}
				else
				{
					System.out.println("Sender is Originator and is allowed to access state");
				}
			}
		}

	}
}
