concern GenericConcern
{
	filtermodule FM1
	{
		inputfilters
			addtorem : Dispatch = { [*.Add] *.AddString }
	}
	
	filtermodule FM2
	{
		// this doesn't do much for a MyStringGeneric because the signatures of the methods do not match.
		// instead Remove(String) is removed, and Remove(int) is added.
		internals
			intgen : GenericsTest.MyIntGeneric;
		inputfilters
			addtorem : Dispatch = { [*.Remove] intgen.Remove }
	}
	
	superimposition
	{
		selectors
			//// this will select the generic type GenericsTest.MyGeneric<T>, it's an internal name
			//// however, it will create a runtime execption in the JPC because the type can not be loaded
			//basegeneric = { C | isClassWithName(C, 'GenericsTest.MyGeneric`1') };
			
			//// this results in an runtime exception because the implied generic type can not be loaded: MyGeneric`1
			//// when only explicit generics are used it will work as intended
			//gens = { C | isClass(C), isMethodWithName(M, 'Add'), classHasMethod(C, M) };
			
			sgens = { C | isClassWithName(C, 'GenericsTest.MyStringGeneric') };
		filtermodules
			//basegeneric <- FM1;
			//gens <- FM1;
			//basegeneric <- FM2;
			sgens <- FM2;
	}
}
