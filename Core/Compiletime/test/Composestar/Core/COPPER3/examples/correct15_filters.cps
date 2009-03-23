// various filter constructions
concern correct05_filters in ConcernExamples
{
	filtermodule simple
	{
		inputfilters
			f1 : Dispatch = (selector == 'foo') { selector = 'bar'; };
			f2 : Dispatch = (selector $= inner) { target = inner; };
			f3 : Dispatch = (selector == 'foo');
			f4 : Dispatch = (selector $= inner);
			f5 : Dispatch = (target ~= this.is.some.type | target @= iam.an.annotation)
	}
	
	filtermodule basicconds
	{
		conditions
			cond : inner.cond();
		inputfilters
			f1 : Dispatch = (True & selector == 'foo');
			f2 : Dispatch = (False & selector == 'foo');
			f3 : Dispatch = (cond & selector == 'foo');
			f4 : Dispatch = (True & !(selector == 'foo'))
	}
	
	filtermodule condexpr
	{
		conditions
			cond1 : inner.cond();
			cond2 : inner.cond();
			cond3 : inner.cond();
		inputfilters
			f0 : Dispatch = ( (cond1) & selector == 'foo' );
			f1 : Dispatch = ( !cond1 & selector == 'foo' );
			f2 : Dispatch = ( !(cond1) & selector == 'foo' );
			f3 : Dispatch = ( cond1 & cond2 & selector == 'foo' );
			f4 : Dispatch = ( cond1 | cond2 & selector == 'foo' );
			f5 : Dispatch = ( (cond1 & cond2) & selector == 'foo' );
			f6 : Dispatch = ( cond1 & (cond2 | cond3) & selector == 'foo' );
			f7 : Dispatch = ( !cond1 & !(!cond2 | !cond3) & selector == 'foo' )
	}
	
	filtermodule elmlists
	{
		inputfilters
			// multiple filter elements
			f1 : Dispatch = (selector == 'foo')
							cor (selector == 'bar')
							cor (selector $= inner) { target = inner; }; 
			// matching pattern list
			f2 : Dispatch = (
					selector == 'foo' | (selector == 'bar' | selector == 'baz')
				) 
				{ selector = 'quux'; };
			f3 : Dispatch = ( selector == ['foo', 'bar', 'baz'] ) { 
				selector = 'wuux';
				message.target = inner; 
			};
			f4 : Dispatch = ( selector == ['foo'] ) { 
				selector = 'wuux';
				message.target = inner; 
			};
			// curly braces are allowed, but shouldn't be used
			f3alt : Dispatch = ( selector == {'foo', 'bar', 'baz'} ) { 
				selector = 'wuux';
				message.target = inner; 
			};
			f4alt : Dispatch = ( selector == {'foo'} ) { 
				selector = 'wuux';
				message.target = inner; 
			}
	}
}
