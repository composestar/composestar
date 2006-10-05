/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 *
**/

header {
package Composestar.CTCommon.FIRE;
}


// Global options
options
{
	mangleLiteralPrefix = "TT_"; // instead of TOKEN
	language="Java";
}

class TT_BuildFireTree extends TreeParser;
options
{
	importVocab=TT_;
}

{
	public SymbolTable symbolTable = SymbolTable.getInstance();
}

program returns [FilterComponent component = null]
{
	FilterComponent child1, child2;
}
   : #(PROGRAM child1=filterExpression)
   {

	//Tand program = new Tand();
	//program.addChild1(new Start());
	//Tand tmp = new Tand();

	Tand program = new Tand();
	program.addChild1(child1);
	program.addChild2(new Action(FilterFactory.getFilter("EOF")));
	//program.addChild2(tmp);

	// Add the star symbols.
	symbolTable.addSymbol("*",1); // Target star
	symbolTable.addSymbol("*",2); // Selector star
	
	component = program;
/*
	StateTable stateTable = StateTable.getInstance();
	System.out.println(stateTable);	
*/
	//program.visit(
/*
	for (int i = 0; i < 3; i++)
	{
		System.out.println (symbolTable.totalSymbols(i));
   	}

	Symbol [] symbols = symbolTable.addSymbol(1);

	for (int i = 0; i < symbols.length; i++)
		System.out.println (symbols[i]);
		*/
   }
   ;

filterExpression returns [FilterComponent component = null]
{
	FilterComponent child1, child2;
}
	: #(COMMA child1 = filterExpression child2 = filterExpression) 
	{
		Tor comma = new Tor();
		comma.addChild1(child1);
		comma.addChild2(child2);
		component = comma;
	}
	| #(SEMI child1 = filterExpression child2 = filterExpression) 
	{
		Tand semi = new Tand();
		semi.addChild1(child1);
		semi.addChild2(child2);
		component = semi;
	}
	| component = filter 
	;

/* 
0: msgMatchSelector
1: msgMatchTarget
2: signatureMatch
20: conditionMatch

10: msgReplaceSelector
11: msgReplaceTarget

30: action
*/


filter returns [FilterComponent component = null]
	{
		FilterComponent child1, child2;
	}
	: #(EQUALS child1 = expression[1] child2 = identifier[30]) 
	{
		Tand eq = new Tand();
		eq.addChild1(child1);
		eq.addChild2(child2);
		component = eq;
   	}
	;


expression[int type] returns [FilterComponent component = null]
	{
		FilterComponent child1, child2;
	}
    :( #(COMMA child1 = expression[type] child2 = expression[type]) 
    | #(OR child1 = expression[0] child2 = expression[0]) 
   ) {
		Tor tor = new Tor();
		tor.addChild1(child1);
		tor.addChild2(child2);
		component = tor;
   }
	|( #(SEMI child1 = expression[type] child2 = expression[type])
    | #(IMPLIES child1 = expression[0] child2 = expression[1]) 
    | #(AND child1 = expression[0] child2 = expression[0]) 
    | #(SLASH child1 = expression[1] child2 = expression[11]) 
    | #(DOT child1 = expression[type] child2 = expression[type + 1]) 
   ) {
   		Tand tand = new Tand();
		tand.addChild1(child1);
		tand.addChild2(child2);
		component = tand;
   }
   	| #(IMPLIESNOT child1 = expression[0] child2 = expression[1]) 
	{
   		TandNot tand = new TandNot();
		tand.addChild1(child1);
		tand.addChild2(child2);
		component = tand;
	}

    | #(NOT child1 = expression[type]) 
	{
		Tnot not = new Tnot();
		not.addChild1(child1);
		component = not;
	}

	| #(SIGNATURE component = expression[20])
	| component = identifier[type]
	;

/* 
20: signatureMatch
0: conditionMatch --> 0
1: msgMatchSelector --> 1
2: msgMatchTarget --> 2 

11: msgReplaceSelector
12: msgReplaceTarget

30: action
*/
identifier[int type] returns [FilterComponent component = null]
	: id:IDENT
	{
		switch (type)
		{
			case 0: component = new Match(symbolTable.addSymbol(id.toString(), 0)); break; // Condition
			case 1: component = new Match(symbolTable.addSymbol(id.toString(), 1)); break; // Target
			case 2: component = new Match(symbolTable.addSymbol(id.toString(), 2)); break; // Selector

			case 11: component = new Substitute(symbolTable.addSymbol(id.toString(), 1)); break; // Target
			case 12: component = new Substitute(symbolTable.addSymbol(id.toString(), 2)); break; // Selector

			case 20: component = new MatchSignature(symbolTable.addSymbol(id.toString(), 1)); break; // Target

			case 30: component = new Action(FilterFactory.getFilter(id.toString())); break;
		}
	}
	| (TRUE | ONE)
	{
		component = new MatchTrue();
	}
	| (FALSE | ZERO)
	{
		component = new MatchFalse();
	}
	| STAR
	{
		switch (type)
		{
		case 0: component = new Match(symbolTable.addSymbol("*", 0)); break; // Condition
		case 1: component = new Match(symbolTable.addSymbol("*", 1)); break; // Target
		case 2: component = new Match(symbolTable.addSymbol("*", 2)); break; // Selector
		}
	}
	;
	

