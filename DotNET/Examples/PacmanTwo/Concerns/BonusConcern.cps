/*
TODO:
	- add pickup sound
	- add levelup bonus changes
*/
concern BonusConern in PackmanTwo
{
	filtermodule BonusManager
	{
		externals
			bm: PacmanTwo.Bonus.Bonus = PacmanTwo.Bonus.Bonus.instance();
		inputfilters
			startBonus : Meta = { [*.startGame] bm.startGame };
			tick : Meta = { [*.tick] bm.tick }
	}

	filtermodule TouchBonus
	{
		externals
			bm: PacmanTwo.Bonus.Bonus = PacmanTwo.Bonus.Bonus.instance();
		inputfilters
			touched : Meta = { [*.touch] bm.pacmanTouch }
	}

	filtermodule RegisterBonusView
	{
		externals
			bm: PacmanTwo.Bonus.Bonus = PacmanTwo.Bonus.Bonus.instance();
		inputfilters
			touched : Meta = { [*.createViews] bm.createViews }
	}

	superimposition
	{
		selectors
			game = { C | isClassWithName(C, 'PacmanTwo.Game') };
			pacman = { C | isClassWithName(C, 'PacmanTwo.Pacman') };
			viewport = { C | isClassWithName(C, 'PacmanTwo.GUI.Viewport') };
			//lvl = { C | isClassWithName(C, 'PacmanTwo.Level') };
		filtermodules
			game <- BonusManager;
			pacman <- TouchBonus;
			viewport <- RegisterBonusView;
	}
}