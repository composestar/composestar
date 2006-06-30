concern InterpreterEvaluate {

	filtermodule EvaluateAndExpression{
		
		internals
			c : Composestar.Patterns.Interpreter.Evaluate;

		inputfilters
			and			: Dispatch = { [*.evaluate] c.evaluateAndExpression }
	}
	filtermodule EvaluateOrExpression{
		
		internals
			c : Composestar.Patterns.Interpreter.Evaluate;

		inputfilters
			or			: Dispatch = { [*.evaluate] c.evaluateOrExpression }
	}
	filtermodule EvaluateNotExpression{
		
		internals
			c : Composestar.Patterns.Interpreter.Evaluate;

		inputfilters
			not			: Dispatch = { [*.evaluate] c.evaluateNotExpression }
	}
	filtermodule EvaluateBooleanConstant{
		
		internals
			c : Composestar.Patterns.Interpreter.Evaluate;

		inputfilters
			constant	: Dispatch = { [*.evaluate] c.evaluateBooleanConstant }
	}
	filtermodule EvaluateVariableExpression{
		
		internals
			c : Composestar.Patterns.Interpreter.Evaluate;

		inputfilters
			variable	: Dispatch = { [*.evaluate] c.evaluateVariableExpression }
	}
	superimposition{
		selectors
			and			= { C | isClassWithName(C,'Composestar.Patterns.Interpreter.AndExpression') };
			or			= { C | isClassWithName(C,'Composestar.Patterns.Interpreter.OrExpression') };
			not			= { C | isClassWithName(C,'Composestar.Patterns.Interpreter.NotExpression') };
			constant	= { C | isClassWithName(C,'Composestar.Patterns.Interpreter.BooleanConstant') };
			variable	= { C | isClassWithName(C,'Composestar.Patterns.Interpreter.VariableExpression') };			

		filtermodules
			and			<- EvaluateAndExpression;
			or			<- EvaluateOrExpression;
			not			<- EvaluateNotExpression;
			constant	<- EvaluateBooleanConstant;
			variable	<- EvaluateVariableExpression;
	}
}