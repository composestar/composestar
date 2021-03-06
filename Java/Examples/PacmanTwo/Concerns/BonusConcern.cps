// Note: Bonus concern is partially broken, the Pacman.touch message is not send
// this is due to the weaving of Compose*/Java
concern BonusConcern in PacmanTwo
{
	filtermodule BonusManager
	{
		externals
			bm: PacmanTwo.Bonus.Bonus = PacmanTwo.Bonus.Bonus.instance();
		inputfilters
			startBonus : Meta = { [*.startNewGame] bm.startGame };
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
	
	filtermodule LevelUp
	{
		externals
			bm: PacmanTwo.Bonus.Bonus = PacmanTwo.Bonus.Bonus.instance();
		inputfilters
			lvlup : Meta = { [*.increaseLevel] bm.levelUp }
	}

	filtermodule BonusPickupSound
	{
		internals
			beeper : PacmanTwo.ConcernImplementations.Beeper;
		inputfilters
			redirBeep : Dispatch = { [*.beep] beeper.beep }
	}

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
			levelgen <- LevelUp;
			bonus <- BonusPickupSound;
	}
}