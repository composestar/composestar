concern InterpreterReplace {

	filtermodule ReplaceAndExpression{
		
		internals
			c : Composestar.Patterns.Interpreter.Replace;

		inputfilters
			and			: Dispatch = { [*.replace] c.replaceAndExpression }
	}
	filtermodule ReplaceOrExpression{
		
		internals
			c : Composestar.Patterns.Interpreter.Replace;

		inputfilters
			or			: Dispatch = { [*.replace] c.replaceOrExpression }
	}
	filtermodule ReplaceNotExpression{
		
		internals
			c : Composestar.Patterns.Interpreter.Replace;

		inputfilters
			not			: Dispatch = { [*.replace] c.replaceNotExpression }
	}
	filtermodule ReplaceBooleanConstant{
		
		internals
			c : Composestar.Patterns.Interpreter.Replace;

		inputfilters
			constant	: Dispatch = { [*.replace] c.replaceBooleanConstant }
	}
	filtermodule ReplaceVariableExpression{
		
		internals
			c : Composestar.Patterns.Interpreter.Replace;

		inputfilters
			variable	: Dispatch = { [*.replace] c.replaceVariableExpression }
	}
	superimposition{
		selectors
			and			= { C | isClassWithName(C,'Composestar.Patterns.Interpreter.AndExpression') };
			or			= { C | isClassWithName(C,'Composestar.Patterns.Interpreter.OrExpression') };
			not			= { C | isClassWithName(C,'Composestar.Patterns.Interpreter.NotExpression') };
			constant	= { C | isClassWithName(C,'Composestar.Patterns.Interpreter.BooleanConstant') };
			variable	= { C | isClassWithName(C,'Composestar.Patterns.Interpreter.VariableExpression') };			

		filtermodules
			and			<- ReplaceAndExpression;
			or			<- ReplaceOrExpression;
			not			<- ReplaceNotExpression;
			constant	<- ReplaceBooleanConstant;
			variable	<- ReplaceVariableExpression;
	}
}