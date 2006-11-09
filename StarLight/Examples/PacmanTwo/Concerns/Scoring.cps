concern Scoring in PacmanTwo
{
	filtermodule registerScorePawns
	{
		externals
			score : PacmanTwo.Scoring.Score = PacmanTwo.Scoring.Score.instance();
		inputfilters
			scored : After = { [*.died] score.pawnDied }
	}

	filtermodule registerScoreLevel
	{
		externals
			score : PacmanTwo.Scoring.Score = PacmanTwo.Scoring.Score.instance();
		inputfilters
			scored : After = { [*.eatPill] score.eatPill
			                 , [*.eatPowerPill] score.eatPowerPill }
	}

	filtermodule renderScore
	{
		internals
			scoreview : PacmanTwo.Scoring.ScoreView;
		inputfilters
			render : After = { [*.renderAll] scoreview.renderScore }
	}

	superimposition
	{
		selectors
			lvl = { C | isClassWithName(C, 'PacmanTwo.Level') };
			pawns = { C | isClassWithNameInList(C, ['PacmanTwo.Ghost']) };
			viewport = { C | isClassWithName(C, 'PacmanTwo.GUI.Viewport' ) };
		filtermodules
			lvl <- registerScoreLevel;
			pawns <- registerScorePawns;
			viewport <- renderScore;
	}
}