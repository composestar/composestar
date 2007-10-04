// various filter module definitions
concern correct04_filtermodules in Concern.Examples
{
	filtermodule empty
	{}
	
	filtermodule oneinput
	{
		inputfilters
			one : Dispatch = { <inner.*> inner.* }
	}
	
	filtermodule oneoutput
	{
		outputfilters
			one : Send = { <inner.*> inner.* }
	}
	
	filtermodule inout
	{
		inputfilters
			one : Dispatch = { <inner.*> inner.* }
		outputfilters
			two : Send = { <inner.*> inner.* }
	}
	
	filtermodule internal
	{
		internals
			vect : Vector;
		inputfilters
			one : Dispatch = { <vect.*> vect.* }
	}
	
	filtermodule external
	{
		externals
			ext : FooBarQuux = FooBarQuux.instance();
		inputfilters
			toext : Dispatch { <ext.*> ext.* }
	}
	
	filtermodule cond
	{
		conditions
			isactive : inner.isActive();
		inputfilters
			accept : Error = { isactive => <inner.*>}
	}
	
	filtermodule inoutintextcond
	{
		internals
			vect : Vector;
		externals
			ext : FooBarQuux = FooBarQuux.instance();
		conditions
			isactive : inner.isActive();			
		inputfilters
			one : Dispatch = { <vect.*> vect.* };
			toext : Dispatch { <ext.*> ext.* };
			accept : Error = { isactive => <inner.*>}
		outputfilters
			two : Send = { <inner.*> inner.* }
	}
	
	filtermodule inoutintextcond_ex
	{
		internals
			vect : Vector;
			vect2 : java.util.Vector;
		externals
			ext : FooBarQuux = FooBarQuux.instance();
			ext2 : Concern.Examples.Base = FooBarQuux.instance();
		conditions
			isactive : inner.isActive();			
			isactive2 : ext2.isActive();
			isactive3 : Concern.Examples.Base.isActive();
		inputfilters
			one : Dispatch = { <vect.*> vect.* };
			toext : Dispatch { <ext.*> ext.* };
			accept : Error = { isactive => <inner.*>}
		outputfilters
			two : Send = { <inner.*> inner.* }
	}
		
}
