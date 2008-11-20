concern AnotherFilterTestsConcern in BasicTests
{
	filtermodule VisitorFM
	{
		internals
			visitor : BasicTests.Visitor;
		conditions
			enoughVisits : visitor.isEnough();
		inputfilters
			error : Error = { True ~> [*.makeTrip], True => <inner.*> };
			makeVisit : Dispatch = { !enoughVisits => [*.makeTrip] *.doVisit };
			countVisit : Meta = { [*.doVisit] visitor.visit };
			makeTrip : Dispatch = { [*.doVisit] *.makeTrip }
	}

	filtermodule OutsideTripFM
	{
		internals
			visitor : BasicTests.Visitor;
		conditions
			enoughVisits : visitor.isEnough();
		inputfilters
			countVisit : Meta = { [*.doOutsideVisit] visitor.visitHome };
			makeTrip : Dispatch = { [*.doOutsideVisit] *.makeOutsideTrip }
		outputfilters
			//makeOutsideTrip : Send = { !enoughVisits => [*.makeTrip] inner.doOutsideVisit }
			makeOutsideTrip : Send = ( !enoughVisits & selector == 'makeTrip' )
				{
					target = message.self;
					selector = 'doOutsideVisit'; 
				}
	}

	superimposition
	{
		selectors
			baseClass = { C | isClassWithName(C, 'BasicTests.FilterTests') };
		filtermodules
			baseClass <- VisitorFM;
			baseClass <- OutsideTripFM;
	}

	implementation in Java by BasicTests.Visitor as "Visitor.java"
	{
		package BasicTests;
		
		public class Visitor
		{
			protected int visits = 0;
	
			public Visitor()
			{
				
			}
	
			public void visit(Object o)
			{
				visits++;
				System.out.println("Visit "+visits);
			}
	
			public void visitHome(Object o)
			{
				visits++;
				System.out.println("It's raining, staying home "+visits+" time(s)");
			}
	
			public boolean isEnough()
			{
				if(visits >= 5)
					return true; 
				return false;
			}
		}
	}
}
