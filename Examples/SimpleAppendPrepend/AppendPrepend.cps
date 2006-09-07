concern AppendPrepend in SimpleAppendPrepend {
	filtermodule AppendPrepend{
		internals
			om : SimpleAppendPrepend.OtherMessages;
		inputfilters
			before : Prepend = { [*.second] om.first };
			after : Dispatch = { #(<om.first>;[*.second]) #(om.first;inner.second;om.third) }
	}
	
	superimposition{
		selectors
			files = { C | isClassWithName(C,'SimpleAppendPrepend.Start') };
		filtermodules
			files <- AppendPrepend;
	}

}