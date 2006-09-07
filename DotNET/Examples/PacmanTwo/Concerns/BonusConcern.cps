/*

TODO:
		first implement:
		- score concern
		- level concern
*/
concern BonusConern in PackmanTwo
{
	filtermodule BonusManager
	{
		externals
			bm: PacmanTwo.Bonus.Bonus = PacmanTwo.Bonus.Bonus.instance();
		inputfilters
			// ...
	}

	superimposition
	{
		selectors
			game = { C | isClassWithName(C, 'PacmanTwo.Game') };
		filtermodules
			game <- BonusManager;
	}
}