concern MementoCreation{
	filtermodule mementoCreation{
		internals
			mementoAccess : Composestar.Patterns.Memento.MementoCreation;
		
		inputfilters
			create		: Dispatch	= { [*.createMemento] mementoAccess.createMemento };
			setMemento	: Dispatch	= { [*.setMemento] mementoAccess.setMemento }
	}
	superimposition{
		selectors
			originator	= { Class | classHasAnnotationWithName(Class, 'Composestar.Patterns.Memento.Annotations.Originator') };
				
		filtermodules
			originator	<- mementoCreation;
	}
	implementation in JSharp by	MementoCreation as	"MementoCreation.jsl"
	{
		package Composestar.Patterns.Memento;

		public class MementoCreation
		{
			public MementoCreation(){}

			public CounterMemento createMemento() 
			{ 
				CounterMemento memento = new CounterMemento(); 
				Counter originator = (Counter)((Object)this);
				//currentValue is protected and this is needed to access it in this class
				memento.setState(originator.currentValue);
				return memento;
			}

			public void setMemento(CounterMemento memento) 
			{ 
				Counter originator = (Counter)((Object)this);
				originator.currentValue = memento.getState(); 
			}
		}
	}
}