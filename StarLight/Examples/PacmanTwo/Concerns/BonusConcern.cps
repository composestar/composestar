/*
TODO:
	- add pickup sound
	- add levelup bonus changes
*/
concern BonusConcern in PacmanTwo
{
	filtermodule BonusManager
	{
		externals
			bm: PacmanTwo.Bonus.Bonus = PacmanTwo.Bonus.Bonus.instance();
		inputfilters
			startBonus : After = { [*.startNewGame] bm.startGame };
			tick : After = { [*.tick] bm.tick }
	}

	filtermodule TouchBonus
	{
		externals
			bm: PacmanTwo.Bonus.Bonus = PacmanTwo.Bonus.Bonus.instance();
		inputfilters
			touched : After = { [*.touch] bm.pacmanTouch }
	}

	filtermodule RegisterBonusView
	{
		externals
			bm: PacmanTwo.Bonus.Bonus = PacmanTwo.Bonus.Bonus.instance();
		inputfilters
			touched : After = { [*.createViews] bm.createViews }
	}
	
	/*
	filtermodule LevelUp
	{
		externals
			bm: PacmanTwo.Bonus.Bonus = PacmanTwo.Bonus.Bonus.instance();
		inputfilters
			lvlup : Meta = { [*.increaseLevelx] bm.levelUp }
	}
	*/
/*
	filtermodule BonusPickupSound
	{
		internals
			beeper : PacmanTwo.ConcernImplementations.Beeper;
		inputfilters
			redirBeep : Dispatch = { [*.beep] beeper.beep }
	}
*/
	superimposition
	{
		selectors
			game = { C | isClassWithName(C, 'PacmanTwo.Game') };
			pacman = { C | isClassWithName(C, 'PacmanTwo.Pacman') };
			viewport = { C | isClassWithName(C, 'PacmanTwo.GUI.Viewport') };
			levelgen = { C | isClassWithName(C, 'PacmanTwo.ConcernImplementations.LevelGenerator') };
			bonus = { C | isClassWithName(C, 'PacmanTwo.Bonus.Bonus') };
		filtermodules
			game <- BonusManager;
			pacman <- TouchBonus;
			viewport <- RegisterBonusView;
		//	levelgen <- LevelUp;
		//	bonus <- BonusPickupSound;
	}
}