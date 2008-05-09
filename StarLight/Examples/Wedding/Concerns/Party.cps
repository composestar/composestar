concern Party in Wedding
{
	filtermodule Party
	{
		internals
			party : Wedding.ConcernImplementations.Party;
		inputfilters
			wedding_party : After ={[*.PreformWedding] party.StartParty}
	}
	filtermodule Catering
	{
		internals
			catering : Wedding.ConcernImplementations.Catering;
		inputfilters
			wedding_diner : After ={[*.StartParty]catering.ServeDinner}
	}

	filtermodule Band
	{
		internals
			band : Wedding.ConcernImplementations.Band;
		inputfilters
			wedding_playing : After ={[*.StartParty] band.Play}
	}

	superimposition
	{
	    selectors
			about = { C | isClassWithName(C, 'Wedding.Person') };
			party = { C | isClassWithName(C, 'Wedding.ConcernImplementations.Party') };
		filtermodules
			about <- Party;
			party <- Catering;
			party <- Band;
		constraints
			pre(Band,Catering);
	}
}