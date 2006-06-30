concern InterpreterCopy {

	filtermodule CopyAndExpression{
		
		internals
			c : Composestar.Patterns.Interpreter.Copy;

		inputfilters
			and			: Dispatch = { [*.copy] c.copyAndExpression }
	}
	filtermodule CopyOrExpression{
		
		internals
			c : Composestar.Patterns.Interpreter.Copy;

		inputfilters
			or			: Dispatch = { [*.copy] c.copyOrExpression }
	}
	filtermodule CopyNotExpression{
		
		internals
			c : Composestar.Patterns.Interpreter.Copy;

		inputfilters
			not			: Dispatch = { [*.copy] c.copyNotExpression }
	}
	filtermodule CopyBooleanConstant{
		
		internals
			c : Composestar.Patterns.Interpreter.Copy;

		inputfilters
			constant	: Dispatch = { [*.copy] c.copyBooleanConstant }
	}
	filtermodule CopyVariableExpression{
		
		internals
			c : Composestar.Patterns.Interpreter.Copy;

		inputfilters
			variable	: Dispatch = { [*.copy] c.copyVariableExpression }
	}
	superimposition{
		selectors
			and			= { C | isClassWithName(C,'Composestar.Patterns.Interpreter.AndExpression') };
			or			= { C | isClassWithName(C,'Composestar.Patterns.Interpreter.OrExpression') };
			not			= { C | isClassWithName(C,'Composestar.Patterns.Interpreter.NotExpression') };
			constant	= { C | isClassWithName(C,'Composestar.Patterns.Interpreter.BooleanConstant') };
			variable	= { C | isClassWithName(C,'Composestar.Patterns.Interpreter.VariableExpression') };			

		filtermodules
			and			<- CopyAndExpression;
			or			<- CopyOrExpression;
			not			<- CopyNotExpression;
			constant	<- CopyBooleanConstant;
			variable	<- CopyVariableExpression;
	}
}