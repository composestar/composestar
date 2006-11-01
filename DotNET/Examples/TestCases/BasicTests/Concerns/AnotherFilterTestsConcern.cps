concern AnotherFilterTestsConcern in BasicTests
{
	filtermodule VisitorFM
	{
		internals
			visitor : BasicTests.Visitor;
		conditions
			enoughVisits : visitor.get_Enough();
		inputfilters
			makeVisit : Dispatch = { !enoughVisits => [*.makeTrip] *.doVisit };
			countVisit : Meta = { [*.doVisit] visitor.visit };
			makeTrip : Dispatch = { [*.doVisit] *.makeTrip }
	}

	filtermodule OutsideTripFM
	{
		internals
			visitor : BasicTests.Visitor;
		conditions
			enoughVisits : visitor.get_Enough();
		inputfilters
			countVisit : Meta = { [*.doOutsideVisit] visitor.visitHome };
			makeTrip : Dispatch = { [*.doOutsideVisit] *.makeOutsideTrip }
		outputfilters
			makeOutsideTrip : Send = { !enoughVisits => [*.makeTrip] inner.doOutsideVisit }
	}

	superimposition
	{
		selectors
			baseClass = { C | isClassWithName(C, 'BasicTests.FilterTests') };
		filtermodules
			baseClass <- VisitorFM;
			baseClass <- OutsideTripFM;
	}

	implementation in CSharp by BasicTests.Visitor as "Visitor.cps"
	{
using System;

namespace BasicTests
{
	public class Visitor
	{
		protected int visits = 0;

		public Visitor()
		{
			//Console.Out.WriteLine("New Visitor");
		}

		public void visit(Object o)
		{
			visits++;
			Console.Out.WriteLine("Visit #{0}", visits);
		}

		public void visitHome(Object o)
		{
			visits++;
			Console.Out.WriteLine("It's raining, staying home #{0} time(s)", visits);
		}

		public bool Enough
		{
			get { return visits >= 5; }
		}
	}
}

	}
}