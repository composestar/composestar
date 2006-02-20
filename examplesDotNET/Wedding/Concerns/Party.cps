concern Party in Wedding
{
	filtermodule Party
	{
		internals
			party : wedding.ConcernImplementations.Party;
		inputfilters
			wedding_party : Meta ={[*.preformWedding] party.startParty}
	}
	filtermodule Catering
	{
		internals
			catering : wedding.ConcernImplementations.Catering;
		inputfilters
			wedding_diner : Meta ={[*.startParty]catering.serveDinner}
	}

	filtermodule Band
	{
		internals
			band : wedding.ConcernImplementations.Band;
		inputfilters
			wedding_playing : Meta ={[*.startParty] band.play}
	}

	superimposition
	{
	    selectors
			about = { C | isClassWithName(C, 'wedding.Person') };
			party = { C | isClassWithName(C, 'wedding.ConcernImplementations.Party') };
		filtermodules
			about <- Party;
			party <- Catering;
			party <- Band;
	}
}