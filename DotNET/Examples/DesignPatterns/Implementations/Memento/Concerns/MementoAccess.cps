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

		import Composestar.RuntimeCore.FLIRT.Message.ReifiedMessage;

		public class MementoAccess
		{
			public MementoAccess(){}

			private Object originator;

			//the object that sets the state is the only one that is allowed to get the state
			public void setOriginator(ReifiedMessage message)
			{
				//memento is not set or memento is set by the originator
				if(!(originator == null) && !(originator == message.getSender()))
				{
					System.out.println("WARNING: State is not allowed to be set by another object then originator");
					message.reply();
				}
				else
				{
					originator = message.getSender();
					System.out.println("Setting Originator: "+originator.getClass());
				}
			}

			public void checkOriginator(ReifiedMessage message) throws Exception
			{
				System.out.println("The sender should be the originator");
				System.out.println("originator "+originator.getClass());
				System.out.println("sender "+message.getSender().getClass());
				if(!(originator == message.getSender()))
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
